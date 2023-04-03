package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class StartEvertCommand(private val teamManager: TeamManager, private val clueManager: ClueManager) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "start") {
            event.deferReply().queue()
            for (team in teamManager.teams) {
                team.textChannel.sendMessage("Get ready! The hunt will start in **30** seconds. " +
                        "You will be competing against **${teamManager.teams.size-1}** other teams. Your team code is `${team.id}`. " +
                        team.members.joinToString(", ") { it.asMention }).queue()
                team.textChannel.sendMessage(clueManager.clues[0].message).queueAfter(30000, TimeUnit.MILLISECONDS)
                team.clueNumber = 1
                teamManager.redisson.getTopic("mchunt").publish("ID:${team.id},${team.name}")
            }
            clueManager.started = true
            event.hook.sendMessage("Event has started!").queue()
        }
    }
}