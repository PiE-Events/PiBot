package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class StartEvertCommand(private val teamManager: TeamManager, private val clueManager: ClueManager) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "start") {
            for (team in teamManager.teams) {
                team.textChannel.sendMessage("Let the games begin! " +
                        team.members.joinToString(", ") { it.asMention }).queue()
                team.textChannel.sendMessage(clueManager.clues[0].message).queue()
                clueManager.activeClues[team.textChannel] = clueManager.clues[0]
            }
            event.interaction.reply("Event has started!").queue()
        }
    }
}