package com.loudbook.dev.scavangerhunt

import com.loudbook.dev.Discord
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel


class ClueParser(private val manager: ClueManager, jda: JDA) : Runnable {
    private val channel = jda.getTextChannelById("1087472366608732281") as TextChannel
    override fun run() {
        val history = MessageHistory.getHistoryFromBeginning(channel).complete()
        val messages: List<Message> = history.retrievedHistory
        for (message in messages) {
            val str = message.contentDisplay
            val cluenum: Int =
                Integer.valueOf(str.substring(str.indexOf("[") + 1,
                    str.indexOf("]")).replace("Clue ", ""))
            val answers: MutableList<String> = ArrayList(
                str.split("[Answer]")[1].split("|"))
            for ((i, answer) in answers.withIndex()) {
                answers[i] = answer.trim()
            }

            var clue = message.contentRaw
            clue = clue.split("[Answer]")[0]
            clue = clue.replace("[Clue $cluenum]", "**Clue #$cluenum**", true)
            manager.clues.add(Clue(cluenum, clue, answers))
        }
        manager.clues.reverse()
    }
}