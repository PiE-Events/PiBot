package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import org.redisson.api.RedissonClient
import java.io.*

class FileManager(private val teamManager: TeamManager, private val jda: JDA, private val redissonClient: RedissonClient) {
    fun save() {
        val file = File("./teams.db")
        if (!file.exists()) {
            file.createNewFile()
        }
        val outputStream = ObjectOutputStream(FileOutputStream(file))
        val serializedTeams: MutableList<SerializedTeam> = mutableListOf()
        for (team in teamManager.teams) {
            serializedTeams.add(team.serialize())
        }
        outputStream.writeObject(serializedTeams)
        outputStream.close()
    }

    fun load() {
        val file = File("./teams.db")
        if (!file.exists()) {
            file.createNewFile()
        }
        val fileInputStream = FileInputStream(file)
        if (fileInputStream.available() == 0) {
            return
        }
        val inputStream = ObjectInputStream(fileInputStream)
        val serializedTeams: List<SerializedTeam> = (inputStream.readObject() as ArrayList<*>).filterIsInstance<SerializedTeam>()
        val teams: MutableList<Team> = mutableListOf()
        for (serializedTeam in serializedTeams) {
            val leader = jda.retrieveUserById(serializedTeam.leader).complete()
            val team = Team(
                jda.getVoiceChannelById(serializedTeam.voiceChannel)?: continue,
                jda.getTextChannelById(serializedTeam.textChannel)?: continue,
                serializedTeam.name,
                leader,
                jda,
                redissonClient,
            )
            for (member in jda.getGuildById(903380581117751406)!!.members) {
                if (member.roles.contains(jda.getRoleById(903380878598766613))) continue
                if (member.roles.contains(jda.getRoleById(1064329538793910393))) continue
                if (!member.hasPermission(team.textChannel, Permission.VIEW_CHANNEL)) continue
                team.members.add(member.user)
                println("Loaded member ${member.user.name}")
            }
            team.id = serializedTeam.id
            teams.add(team)
            println("Loaded team ${serializedTeam.name}")
        }
        teamManager.teams = teams
        inputStream.close()
    }
}