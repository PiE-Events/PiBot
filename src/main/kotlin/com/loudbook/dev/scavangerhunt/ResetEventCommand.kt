package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ResetEventCommand(private val teamManager: TeamManager,
                        private val clueManager: ClueManager,
                        private val clueParser: ClueParser) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "reset") {
            event.interaction.reply("Resetting event...").queue()
            teamManager.teams.forEach { it ->
                it.members.forEach {
                    teamManager.getTeam(it)!!.removeMember(it)
                }
            }
            clueManager.clues.forEach {
                clueManager.clues.remove(it)
            }
            clueParser.run()
            event.interaction.reply("Event has been reset!").queue()
        }
    }
}