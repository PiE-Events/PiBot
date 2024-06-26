package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel


class ClueParser(private val manager: ClueManager, jda: JDA) {
    private val channel = jda.getTextChannelById("1087472366608732281") as TextChannel
    fun run(reAdd: Boolean = false) {
        val history = MessageHistory.getHistoryFromBeginning(channel).complete()
        val messages: List<Message> = history.retrievedHistory
        for (message in messages) {
            val str = message.contentDisplay

            val cluenum: Int
            var minecraft = false
            try {
                if (str.substring(
                        str.indexOf("[") + 1,
                        str.indexOf("]")
                    ).contains("MClue")) {
                    cluenum =
                        Integer.valueOf(
                            str.substring(
                                str.indexOf("[") + 1,
                                str.indexOf("]")
                            ).replace("MClue ", "")
                        )
                    println("Detected Minecraft clue! $cluenum")
                    minecraft = true
                } else {
                    cluenum =
                        Integer.valueOf(
                            str.substring(
                                str.indexOf("[") + 1,
                                str.indexOf("]")
                            ).replace("Clue ", "")
                        )
                }
            } catch (e: Exception) {
                println("Error parsing clue!")
                continue
            }

            val answers: MutableList<String> = try {
                ArrayList(
                    str.split("[Answer]")[1].split("|")
                )
            } catch (e: Exception) {
                mutableListOf()
            }
            for ((i, answer) in answers.withIndex()) {
                answers[i] = answer.trim()
            }

            var clue = message.contentRaw
            clue = clue.split("[Answer]")[0]
            clue = clue.replace("[Clue $cluenum]", "**Clue #$cluenum**", true)
            clue = clue.replace("[MClue $cluenum]", "**Clue #$cluenum**", true)
            if (reAdd) {
                for (clue1 in manager.clues) {
                    if (clue1.number == cluenum) {
                        clue1.message = clue
                        clue1.minecraft = minecraft
                        clue1.answers = answers
                    }
                }
            } else {
                manager.clues.add(Clue(cluenum, clue, minecraft, answers))
            }
        }
        if (!reAdd) {
            manager.clues.sortBy { it.number }
        }
    }
}