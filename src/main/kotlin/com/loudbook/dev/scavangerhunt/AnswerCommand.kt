package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class AnswerCommand(private val clueManager: ClueManager, private val teamManager: TeamManager) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "answer") {
            if (!clueManager.started) {
                event.hook.sendMessage("The event has not started yet!").queue()
                return
            }
            clueManager.answerEvent(teamManager.getTeam(event.channel.asTextChannel())!!, event.interaction)
        }
    }
}