package com.loudbook.dev.scavangerhunt

import com.loudbook.dev.Discord
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import java.awt.Color
import java.util.concurrent.TimeUnit

class ClueManager(jda: JDA, private val teamManager: TeamManager) {
    val clues: MutableList<Clue> = ArrayList()
    var started: Boolean = false
    private val possibleAnswers: List<String> = listOf("You got it!", "Good job!", "Amazing!", "Correct!")
    private val possibleNopes: List<String> = listOf("Nope!", "That's not it...", "Luke disapproves.", "Nuh-uh.")
    private var currentWinners = 0
    private val guild = jda.getGuildById(Discord.guildID)?: throw NullPointerException("Guild is null!")
    private val logChannel = guild.getTextChannelById(903825273965903882)
    private val answerLogChannel = guild.getTextChannelById(904182216932855808)
    private val progressLogChannel = guild.getTextChannelById(906608119558336512)

    private fun getClueByNumber(number: Int): Clue? {
        for (clue in clues) {
            if (clue.number == number) {
                return clue
            }
        }
        return null
    }

    private fun getClueByChannel(textChannel: TextChannel): Clue? {
        for (team in teamManager.teams) {
            if (team.textChannel != textChannel) continue
            return clues.filter { it.number == team.clueNumber }[0]
        }
        return null
    }

    fun answerEvent(team: Team, interaction: SlashCommandInteraction? = null) {
        logChannel ?: throw NullPointerException("Log channel is null!")
        answerLogChannel ?: throw NullPointerException("Answer log channel is null!")
        progressLogChannel ?: throw NullPointerException("Progress log channel is null!")

        val channel = interaction?.channel as TextChannel
        val clue = getClueByChannel(channel)

        if (clue == null) {
            interaction.hook.sendMessage("There is no active clue in this channel!").queue()
            return
        }

        var answer = interaction.getOption("answer")?.asString

        if (answer == null) {
            interaction.hook.sendMessage("You must provide an answer!").queue()
            return
        }

        answer = answer.replace("[", "").replace("]", "")

        val ebLog = EmbedBuilder()
        ebLog.setDescription("**#${clue.number}:** [${team.name}] ${interaction.user.asMention} - $answer")
        ebLog.setColor(Color.GRAY)
        answerLogChannel.sendMessageEmbeds(ebLog.build()).queue()

        if (clue.isAnswer(answer)) {
            interaction.hook.sendMessage(possibleAnswers.random()).queue()
            executeAnswer(team, clue)
        } else {
            interaction.hook.sendMessage(possibleNopes.random()).queue()
        }
    }

    fun executeAnswer(team: Team, clue: Clue) {
        val nextClue = getClueByNumber(clue.number + 1)
        if (nextClue == null) {
            currentWinners++
            team.textChannel.sendMessage(":tada: You have completed the scavenger hunt! You are team **#$currentWinners** to finish. :tada:").queue()
            if (currentWinners <=3) {
                val eb = EmbedBuilder()
                val place: String = when (currentWinners) {
                    1 -> {
                        "First"
                    }
                    2 -> {
                        "Second"
                    }
                    else -> {
                        "Third"
                    }
                }
                val color: Color = when (currentWinners) {
                    1 -> {
                        Color(255, 215, 0)
                    }
                    2 -> {
                        Color(192,192,192)
                    }
                    else -> {
                        Color(205, 127, 50)
                    }
                }
                eb.setTitle(":partying_face: $place Place :partying_face:")
                eb.setDescription("Team **${team.name}** has completed the hunt!")
                eb.setFooter("PiEvents Scavenger Hunt", "https://cdn.discordapp.com/attachments/985583793698140240/1068928440255926322/PiE_1.png")
                eb.setColor(color)
                progressLogChannel!!.sendMessageEmbeds(eb.build()).queue()
            }
            return
        }

        if (!clue.answered) {
            val eb = EmbedBuilder()
            eb.setTitle("Clue ${clue.number} Completed! :tada:")
            eb.setDescription("Team **${team.name}** has completed clue **${clue.number}** first!")
            eb.setFooter("PiEvents Scavenger Hunt", "https://cdn.discordapp.com/attachments/985583793698140240/1068928440255926322/PiE_1.png")
            eb.setColor(Color.GREEN)
            progressLogChannel!!.sendMessageEmbeds(eb.build()).queue()
            clue.answered = true
        }

        val eb = EmbedBuilder()
        eb.setDescription("Team **${team.name}** has completed clue ${clue.number}.")
        eb.setColor(Color.GREEN)
        logChannel!!.sendMessageEmbeds(eb.build()).queue()
        team.clueNumber++
        team.textChannel.sendMessage(nextClue.message).queueAfter(2000, TimeUnit.MILLISECONDS)
    }
}