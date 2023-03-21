package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.Component
import net.dv8tion.jda.api.interactions.components.buttons.Button

class TeamCommand(private val teamManager: TeamManager) : ListenerAdapter() {
    private val map = mutableMapOf<User, Team>()
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "teamcreate") {
            event.reply(":tada: Team **${event.interaction.options[0].asString}** has been created!").queue()
            if (teamManager.getTeam(event.interaction.user) != null) {
                event.interaction.reply("You are already in a team!").queue()
                return
            }
            if (teamManager.jda!!.getVoiceChannelsByName(event.interaction.options[0].asString, true).isNotEmpty()) {
                event.interaction.reply("That team already exists!").queue()
                return
            }
            teamManager.addTeam(event.interaction.options[0].asString, event.interaction.user)
        }
        if (event.interaction.name == "invite") {
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.interaction.reply("You are not in a team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user)!!.leader != event.interaction.user) {
                event.interaction.reply("You are not the leader of your team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.options[0].asUser) != null) {
                event.interaction.reply("That user is already in a team!").queue()
                return
            }
            event.reply("${event.interaction.options[0].asUser.asMention} you have been invited to join ${teamManager.getTeam(event.interaction.user)!!.name}!")
                .addActionRow(Button.primary("accept", "Accept"))
                .queue()
            map[event.interaction.options[0].asUser] = teamManager.getTeam(event.interaction.user)!!
        }
        if (event.interaction.name == "leaveteam") {
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.interaction.reply("You are not in a team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user)!!.leader == event.interaction.user) {
                event.interaction.reply("You are the leader of this team, and it has been disbanded!").queue()
                teamManager.getTeam(event.interaction.user)!!.members.forEach {
                    teamManager.getTeam(event.interaction.user)!!.removeMember(it)
                    event.interaction.channel.sendMessage(
                        "${it.asMention} you have been removed from ${teamManager.getTeam(event.interaction.user)!!.name}!").queue()
                }
                return
            } else {
                teamManager.getTeam(event.interaction.user)!!.removeMember(event.interaction.user)
                event.interaction.reply("You have left ${teamManager.getTeam(event.interaction.user)!!.name}!").queue()
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (!map.containsKey(event.user)) {
            event.interaction.reply("That's no longer valid!").queue()
            return
        }
        map[event.interaction.user]!!.addMember(event.interaction.user)
        event.interaction.reply("${event.interaction.user.asMention} has joined ${map[event.interaction.user]!!.name}!").queue()
        map.remove(event.interaction.user)
    }
}