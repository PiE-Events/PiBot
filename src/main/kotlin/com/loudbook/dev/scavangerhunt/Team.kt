package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import java.util.*
import kotlin.collections.ArrayList

class Team(val voiceChannel: VoiceChannel, val textChannel: TextChannel, val name: String, val leader: User, val jda: JDA) {
    val members: MutableList<User> = ArrayList()
    fun addMember(user: User) {
        this.members.add(user)
        voiceChannel.manager.putMemberPermissionOverride(
            user.idLong,
            EnumSet.of(Permission.VIEW_CHANNEL),
            null)
            .queue()
        textChannel.manager.putMemberPermissionOverride(
            user.idLong,
            EnumSet.of(Permission.VIEW_CHANNEL),
            null)
            .queue()
    }

    fun removeMember(user: User) {
        members.remove(user)
        disconnectAndHideUser(user)
    }

    fun clearMembers() {
        for (user in members) {
            disconnectAndHideUser(user)
        }
        members.clear()
    }

    private fun disconnectAndHideUser(user: User) {
        this.voiceChannel.manager.putMemberPermissionOverride(
            user.idLong,
            null,
            EnumSet.of(Permission.VIEW_CHANNEL))
            .queue()
        textChannel.manager.putMemberPermissionOverride(
            user.idLong,
            null,
            EnumSet.of(Permission.VIEW_CHANNEL))
            .queue()
        val member = jda.getGuildById(903380581117751406)!!.getMember(user)!!
        if (this.voiceChannel.members.contains(member)) {
            jda.getGuildById(903380581117751406)!!.kickVoiceMember(member).queue()
        }
    }
}