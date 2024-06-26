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
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.awt.Color

@Suppress("unused")
class Discord {
    lateinit var jda: JDA
    companion object {
        var guildID = 903380581117751406
    }
    fun connect(token: String) {
        jda = JDABuilder.createDefault(token)
            .setChunkingFilter(ChunkingFilter.ALL)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .setEnableShutdownHook(false)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .build().awaitReady()
//        jda.updateCommands().queue()
/*        jda.getGuildById(guildID)!!.updateCommands().addCommands(
            Commands.slash("teamcreate", "Create a team.")
                .addOptions(OptionData(OptionType.STRING, "name", "The name of the team.").setRequired(true)),
            Commands.slash("teamleave", "Leave your team."),
            Commands.slash("invite", "Invite a user.")
                .addOptions(OptionData(OptionType.USER, "user", "The user to invite.").setRequired(true)),
            Commands.slash("answer", "Answer a question.")
                .addOptions(OptionData(OptionType.STRING, "answer", "The answer to the question.").setRequired(true)),
            Commands.slash("start", "Start the game.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
            Commands.slash("reset", "Reset the game.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
            Commands.slash("teamlist", "List the members in your team."),
            Commands.slash("allteamlist", "List all the teams."),
            Commands.slash("reparse", "Reparse the clues.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
            Commands.slash("debug", "Debug command.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
            Commands.slash("pushteams", "Push the teams to Redis!.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
            Commands.slash("removeteam", "Remove a team.")
                .addOptions(OptionData(OptionType.STRING, "name", "The name of the team.").setRequired(true))
                .addOptions(OptionData(OptionType.STRING, "reason", "Reason of removal.").setRequired(true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
            ).queue()*/

    }
    fun sendMCEmbed(sender: String, message: String) {
        val embedBuilder = EmbedBuilder()
        println("Sending message from $sender to Discord: $message")
        embedBuilder.setAuthor(sender, null, "https://minotar.net/helm/$sender")
        embedBuilder.setDescription(message)
        embedBuilder.setColor(Color.GREEN)
        jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCJoinEmbed(sender: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$sender has joined the game!", null, "https://minotar.net/helm/$sender")
        embedBuilder.setColor(Color.GREEN)
        jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCLeaveEmbed(sender: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$sender has left the game!", null, "https://minotar.net/helm/$sender")
        embedBuilder.setColor(Color.RED)
        jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCDeathEmbed(killer: String, dead: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$dead has been slain by $killer!", null, "https://minotar.net/helm/$dead")
        embedBuilder.setColor(Color.RED)
        jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
}