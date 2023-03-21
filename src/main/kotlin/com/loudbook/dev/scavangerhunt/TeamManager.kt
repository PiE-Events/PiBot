package com.loudbook.dev.scavangerhunt

import com.github.javafaker.Faker
import com.loudbook.dev.Discord
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import java.util.*

class TeamManager {
    val teams = mutableListOf<Team>()
    val jda = Discord.jda

    fun addTeam(teamName: String, leader: User) {
        println("added team1")
        val guild = jda!!.getGuildById(Discord.guildID)
        jda.getGuildById(guild!!.id)!!.createVoiceChannel(teamName)
            .addRolePermissionOverride(guild.publicRole.idLong, null, EnumSet.of(Permission.VIEW_CHANNEL))
            .setParent(guild.getCategoryById(1072558723895140362))
            .queue()

        jda.getGuildById(guild.id)!!.createTextChannel(teamName)
            .addRolePermissionOverride(guild.publicRole.idLong, null, EnumSet.of(Permission.VIEW_CHANNEL))
            .setParent(guild.getCategoryById(1072558723895140362))
            .queue()

        var voice: VoiceChannel? = null
        var text: TextChannel? = null
        for (channel in guild.channels) {
            if (channel.name == teamName && channel.type == ChannelType.VOICE) {
                voice = channel as VoiceChannel
            }
            if (channel.name == teamName && channel.type == ChannelType.TEXT) {
                text = channel as TextChannel
            }
        }

        println("pppp")
        val team = Team(voice!!, text!!, teamName, leader)
        println("2added team")
        team.addMember(leader)
        println("1added team")
        this.teams.add(team)
        println("added team")
        println(this.teams)
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
            println(team.members)
            if (team.members.contains(user)) {
                return team
            }
        }
        return null
    }
}