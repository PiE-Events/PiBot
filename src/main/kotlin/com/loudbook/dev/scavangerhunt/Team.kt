package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import java.util.*
import kotlin.collections.ArrayList

class Team(val voiceChannel: VoiceChannel, val textChannel: TextChannel, val name: String, val leader: User) {
    val members: MutableList<User> = ArrayList()

    init {
        println("PPPPP")
    }
    fun addMember(user: User) {
        println("og god")
        this.members.add(user)
        println("ds")
        voiceChannel.manager.putMemberPermissionOverride(
            user.idLong,
            EnumSet.of(Permission.VIEW_CHANNEL),
            null)
            .queue()
        println("dsdsds")
        textChannel.manager.putMemberPermissionOverride(
            user.idLong,
            EnumSet.of(Permission.VIEW_CHANNEL),
            null)
            .queue()
    }

    fun removeMember(user: User) {
        members.remove(user)
        voiceChannel.manager.putMemberPermissionOverride(
            user.idLong,
            null,
            EnumSet.of(Permission.VIEW_CHANNEL))
            .queue()
        textChannel.manager.putMemberPermissionOverride(
            user.idLong,
            null,
            EnumSet.of(Permission.VIEW_CHANNEL))
            .queue()
    }
}