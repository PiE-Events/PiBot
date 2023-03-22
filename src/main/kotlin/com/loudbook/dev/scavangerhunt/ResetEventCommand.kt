package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ResetEventCommand(private val teamManager: TeamManager,
                        private val clueManager: ClueManager,
                        private val clueParser: ClueParser) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "reset") {
            teamManager.teams.clear()
            clueManager.clues.clear()
            clueParser.run()
            event.interaction.reply("Event has been reset!").queue()
        }
    }
}