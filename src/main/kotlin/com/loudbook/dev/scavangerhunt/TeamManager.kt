package com.loudbook.dev.scavangerhunt

import com.loudbook.dev.Discord
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import okhttp3.internal.wait
import java.util.*

class TeamManager {
    val teams = mutableListOf<Team>()
    val jda = Discord.jda

    fun addTeam(teamName: String, leader: User) {
        val guild = jda!!.getGuildById(Discord.guildID)
        try {
            jda.getGuildById(guild!!.id)!!.createVoiceChannel(teamName)
                .addRolePermissionOverride(guild.publicRole.idLong, null, EnumSet.of(Permission.VIEW_CHANNEL))
                .setParent(guild.getCategoryById(1072558723895140362))
                .complete()

            jda.getGuildById(guild.id)!!.createTextChannel(teamName)
                .addRolePermissionOverride(guild.publicRole.idLong, null, EnumSet.of(Permission.VIEW_CHANNEL))
                .setParent(guild.getCategoryById(1072558723895140362))
                .complete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var voice: VoiceChannel = jda.getVoiceChannelsByName(teamName, true)[0]
        var text: TextChannel = jda.getTextChannelsByName(teamName.replace(" ", "-"), true)[0]

        val team = Team(voice,
            text,
            teamName,
            leader)

        team.addMember(leader)
        this.teams.add(team)
    }

    fun getTeam(voiceChannel: VoiceChannel): Team? {
        for (team in teams) {
            if (team.voiceChannel == voiceChannel) {
                return team
            }
        }
        return null
    }

    fun getTeam(textChannel: TextChannel): Team? {
        for (team in teams) {
            if (team.textChannel == textChannel) {
                return team
            }
        }
        return null
    }

    fun getTeam(user: User): Team? {
        for (team in teams) {
            if (team.members.contains(user)) {
                return team
            }
        }
        return null
    }
}