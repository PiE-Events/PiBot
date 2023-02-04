package com.loudbook.dev

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import java.awt.Color

class Discord {
    private lateinit var jda: JDA
    fun connect(token: String) {
        this.jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .build().awaitReady()
    }
    fun sendMCEmbed(sender: String, message: String) {
        val embedBuilder = EmbedBuilder()
        println("Sending message from $sender to Discord: $message")
        embedBuilder.setAuthor(sender, null, "https://minotar.net/helm/$sender")
        embedBuilder.setDescription(message)
        embedBuilder.setColor(Color.GREEN)
        this.jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCJoinEmbed(sender: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$sender has joined the game!", null, "https://minotar.net/helm/$sender")
        embedBuilder.setColor(Color.GREEN)
        this.jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCLeaveEmbed(sender: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$sender has left the game!", null, "https://minotar.net/helm/$sender")
        embedBuilder.setColor(Color.RED)
        this.jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
    fun sendMCDeathEmbed(killer: String, dead: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setAuthor("$dead has been slain by $killer!", null, "https://minotar.net/helm/$dead")
        embedBuilder.setColor(Color.RED)
        this.jda.getTextChannelById(1071474941108682926)!!.sendMessageEmbeds(embedBuilder.build()).queue()
    }
}