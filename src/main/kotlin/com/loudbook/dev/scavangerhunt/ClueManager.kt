package com.loudbook.dev.scavangerhunt

import com.loudbook.dev.Discord
import kotlinx.coroutines.flow.combineTransform
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import kotlin.math.log

class ClueManager {
    val clues: MutableList<Clue> = ArrayList()
    private val jda = Discord.jda
    val activeClues: MutableMap<TextChannel, Clue> = HashMap()
    private val possibleAnswers: List<String> = listOf("You got it!", "Good job!", "Amazing!", "Correct!")
    private val possibleNopes: List<String> = listOf("Nope!", "That's not it...", "Luke disapproves.", "Nuh-uh.")
    private var currentWinners = 0
    private val logChannel = jda!!.getGuildById(Discord.guildID)!!.getTextChannelById(903825273965903882)
    private val answerLogChannel = jda!!.getGuildById(Discord.guildID)!!.getTextChannelById(904182216932855808)
    private val progressLogChannel = jda!!.getGuildById(Discord.guildID)!!.getTextChannelById(906608119558336512)

    fun getClueByNumber(number: Int): Clue? {
        for (clue in clues) {
            if (clue.number == number) {
                return clue
            }
        }
        return null
    }

    fun getClueByChannel(textChannel: TextChannel): Clue? {
        for (activeClue in activeClues) {
            if (activeClue.key != textChannel) continue
            return activeClue.value
        }
        return null
    }

    fun answerEvent(interaction: SlashCommandInteraction) {
        val channel = interaction.channel as TextChannel
        val clue = getClueByChannel(channel)
        if (clue == null) {
            interaction.reply("There is no active clue in this channel!").queue()
            return
        }

        var answer = interaction.getOption("answer")?.asString

        if (answer == null) {
            interaction.reply("You must provide an answer!").queue()
            return
        }

        answer = answer.replace("[", "").replace("]", "")

        if (clue.isAnswer(answer)) {
            interaction.reply(possibleAnswers.random()).queue()
            val nextClue = getClueByNumber(clue.number + 1)
            if (nextClue == null) {
                interaction.reply(":tada: You have completed the scavenger hunt! You are team **#$currentWinners** to finish. :tada:").queue()
                answerLogChannel!!.sendMessage(":tada: Team **${channel.name}** has completed the scavenger hunt in place **#$currentWinners**").queue()
                interaction.channel.delete().queue()
                currentWinners++
                return
            }

            if (!clue.answered) {
                progressLogChannel!!.sendMessage(":tada: Team **${channel.name}** has completed **Clue ${clue.number}** first! :tada:").queue()
                clue.answered = true
            } else {
                progressLogChannel!!.sendMessage(":white_check_mark: Team ${channel.name} has completed clue ${clue.number}.").queue()
            }

            logChannel!!.sendMessage("Team ${channel.name} has completed clue ${clue.number}.").queue()
            activeClues[channel] = nextClue
            interaction.channel.sendMessage(nextClue.message).queue()
        } else {
            interaction.reply(possibleNopes.random()).queue()
            logChannel!!.sendMessage(":x: Team **${channel.name}** guessed **$answer** for **Clue ${clue.number}**").queue()
        }
    }
}