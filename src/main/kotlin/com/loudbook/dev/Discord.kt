package com.loudbook.dev

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent
import java.awt.Color

class Discord {
    companion object {
        var jda: JDA? = null
        var guildID = 903380581117751406
    }
    fun connect(token: String) {
        jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .setEnableShutdownHook(false)
            .build().awaitReady()
        jda!!.updateCommands().addCommands(
            Commands.slash("teamcreate", "Create a team")
                .addOptions(OptionData(OptionType.STRING, "name", "The name of the team").setRequired(true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("teamleave", "Leave your team")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("invite", "Invite a user")
                .addOptions(OptionData(OptionType.USER, "user", "The user to invite").setRequired(true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("answer", "Answer a question")
                .addOptions(OptionData(OptionType.STRING, "answer", "The answer to the question").setRequired(true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("start", "Start the game")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("reset", "reset the game")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("teamlist", "List the members in your team")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            Commands.slash("allteamlist", "List all the teams")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MODERATE_MEMBERS)),
            ).queue()
    }
    fun sendMCEmbed(sender: String, message: String) {
        val embedBuilder = EmbedBuilder()
        println("Sending message from $sender to Discord: $message")
        embedBuilder.setAuthor(sender, null, "https://minotar.net/helm/$sender")
        embedBuilder.setDescription(message)
        embedBuilder.setColor(Color.GREEN)
        jda!!.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCJoinEmbed(sender: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$sender has joined the game!", null, "https://minotar.net/helm/$sender")
        embedBuilder.setColor(Color.GREEN)
        jda!!.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCLeaveEmbed(sender: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$sender has left the game!", null, "https://minotar.net/helm/$sender")
        embedBuilder.setColor(Color.RED)
        jda!!.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCDeathEmbed(killer: String, dead: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$dead has been slain by $killer!", null, "https://minotar.net/helm/$dead")
        embedBuilder.setColor(Color.RED)
        jda!!.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
}