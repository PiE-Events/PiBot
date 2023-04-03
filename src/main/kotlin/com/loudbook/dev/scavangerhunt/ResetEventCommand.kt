package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File

class ResetEventCommand(private val teamManager: TeamManager,
                        private val clueManager: ClueManager,
                        private val clueParser: ClueParser) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "reset") {
            event.deferReply().queue()
            event.hook.sendMessage("This command is currently disabled.").queue()
            return

            for (team in teamManager.teams) {
                team.textChannel.delete().complete()
                team.voiceChannel.delete().complete()
            }
            teamManager.teams.clear()
            clueManager.clues.clear()
            File("teams.db").delete()
            clueManager.started = false
            clueParser.run()
            event.hook.sendMessage("Event has been reset!").queue()
        }
        if (event.interaction.name == "reparse") {
            clueParser.run()
            event.hook.sendMessage("Clues have been parsed!").queue()
        }
    }
}