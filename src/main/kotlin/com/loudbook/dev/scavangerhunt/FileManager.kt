package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.JDA
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
            teams.add(
                Team(
                    jda.getVoiceChannelById(serializedTeam.voiceChannel)?: continue,
                    jda.getTextChannelById(serializedTeam.textChannel)?: continue,
                    serializedTeam.name,
                    leader,
                    jda,
                    redissonClient
                )
            )
            println("Loaded team ${serializedTeam.name}")
        }
        teamManager.teams = teams
        inputStream.close()
    }
}