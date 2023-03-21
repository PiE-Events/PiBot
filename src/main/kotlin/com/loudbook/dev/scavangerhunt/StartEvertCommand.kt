package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class StartEvertCommand(private val teamManager: TeamManager, private val clueManager: ClueManager) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "start") {
            event.interaction.reply("Starting event...").queue()
            for (team in teamManager.teams) {
                team.textChannel.sendMessage("The event has started!").queue()
                team.textChannel.sendMessage(clueManager.clues[0].message).queue()
            }
            event.interaction.reply("Event has started!").queue()
        }
    }
}