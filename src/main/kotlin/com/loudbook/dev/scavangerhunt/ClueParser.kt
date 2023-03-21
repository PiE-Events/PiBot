package com.loudbook.dev.scavangerhunt

import com.loudbook.dev.Discord
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel


class ClueParser(private val manager: ClueManager) : Runnable {
    private val channel = Discord.jda!!.getTextChannelById("1087472366608732281") as TextChannel
    override fun run() {
        val history = MessageHistory.getHistoryFromBeginning(channel).complete()
        val messages: List<Message> = history.retrievedHistory
        for (message in messages) {
            val str = message.contentDisplay
            val cluenum: Int =
                Integer.valueOf(str.substring(str.indexOf("[") + 1,
                    str.indexOf("]")).replace("Clue ", ""))
            val answers: MutableList<String> = ArrayList(
                str.substring(str.indexOf("[Answer]") + 1).split("|"))

            for ((i, answer) in answers.withIndex()) {
                answers[i] = answer.trim()
            }
            manager.clues.add(Clue(cluenum, message.contentRaw, answers))
        }
    }
}