package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AnswerCommand(private val clueManager: ClueManager, private val teamManager: TeamManager) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "answer") {
            val team = teamManager.getTeam(event.user)?: run {
                event.reply("You are not in a team!").setEphemeral(true).queue()
                return
            }

            if (event.channel != team.textChannel) {
                event.reply("This command is only enabled in your team's channel!").setEphemeral(true).queue()
                return
            }

            if (!clueManager.started) {
                event.reply("The event has not started yet!").queue()
                return
            }
            event.deferReply()
            clueManager.answerEvent(teamManager.getTeam(event.channel.asTextChannel())!!, event.interaction)
        }
    }
}