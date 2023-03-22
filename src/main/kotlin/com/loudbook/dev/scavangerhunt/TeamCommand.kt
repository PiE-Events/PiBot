package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button

class TeamCommand(private val teamManager: TeamManager) : ListenerAdapter() {
    private val map = mutableMapOf<User, Team>()
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "teamcreate") {
            if (teamManager.getTeam(event.interaction.user) != null) {
                event.interaction.reply("You are already in a team!").queue()
                return
            }
            if (teamManager.jda!!.getVoiceChannelsByName(event.interaction.options[0].asString, true).isNotEmpty()) {
                event.interaction.reply("That team already exists!").queue()
                return
            }
            if (!isLetters(event.interaction.options[0].asString)) {
                event.interaction.reply("Team names can only contain letters!").queue()
                return
            }
            teamManager.addTeam(event.interaction.options[0].asString, event.interaction.user)
            event.reply(":tada: Team **${event.interaction.options[0].asString}** has been created!").queue()
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
            event.reply("${event.interaction.options[0].asUser.asMention} you have been invited to join **${teamManager.getTeam(event.interaction.user)!!.name}**!")
                .addActionRow(Button.success("accept", "Accept"))
                .queue()
            map[event.interaction.options[0].asUser] = teamManager.getTeam(event.interaction.user)!!
        }
        if (event.interaction.name == "teamleave") {
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.interaction.reply("You are not in a team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user)!!.leader == event.interaction.user) {
                event.interaction.reply("You are the leader of this team, and it has been disbanded!").queue()
                teamManager.getTeam(event.interaction.user)!!.members.forEach {
                    event.interaction.channel.sendMessage(
                        "${it.asMention} you have been removed from **${teamManager.getTeam(event.interaction.user)!!.name}**!").queue()
                }
                teamManager.getTeam(event.interaction.user)!!.clearMembers()
                teamManager.teams.remove(teamManager.getTeam(event.interaction.user)!!)
                return
            } else {
                event.interaction.reply("You have left **${teamManager.getTeam(event.interaction.user)!!.name}**!").queue()
                teamManager.getTeam(event.interaction.user)!!.removeMember(event.interaction.user)
            }
        }
        if (event.interaction.name == "teamlist") {
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.interaction.reply("You are not in a team!").queue()
                return
            }
            event.interaction.reply("Members of **${teamManager.getTeam(event.interaction.user)!!.name}**:\n • " +
                    teamManager.getTeam(event.interaction.user)!!.members.joinToString("\n • ") { it.name }).queue()
        }
        if (event.interaction.name == "allteamlist") {
            event.interaction.reply("All teams:\n • " +
                    teamManager.teams.joinToString("\n • ") { it.name }).queue()
        }
    }

    fun isLetters(string: String): Boolean {
        return string.filter { it in 'A'..'Z' || it in 'a'..'z' || it in " "  }.length == string.length
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (!map.containsKey(event.user)) {
            event.interaction.reply("That's no longer valid!").queue()
            return
        }
        map[event.interaction.user]!!.addMember(event.interaction.user)
        event.interaction.reply("${ map[event.interaction.user]!!.leader.asMention}, ${event.user.name} has joined **${map[event.interaction.user]!!.name}**!").queue()
        map.remove(event.interaction.user)
    }
}