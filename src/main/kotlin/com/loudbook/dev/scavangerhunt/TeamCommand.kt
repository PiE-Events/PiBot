package com.loudbook.dev.scavangerhunt

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.redisson.api.RedissonClient

class TeamCommand(private val teamManager: TeamManager, private val clueManager: ClueManager, private val fileManager: FileManager, private val redissonClient: RedissonClient) : ListenerAdapter() {
    private val map = mutableMapOf<User, Team>()
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()
        if (event.channel.id != "1087493041419464794") {
            event.hook.sendMessage("This command can only be used in <#1087493041419464794>!").queue()
            return
        }
        if (event.interaction.name == "teamcreate") {
            if (clueManager.started) {
                event.hook.sendMessage("The event has started already!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user) != null) {
                event.hook.sendMessage("You are already in a team!").queue()
                return
            }
            if (teamManager.jda.getVoiceChannelsByName(event.interaction.options[0].asString, true).isNotEmpty()) {
                event.hook.sendMessage("That team already exists!").queue()
                return
            }
            if (!isLetters(event.interaction.options[0].asString)) {
                event.hook.sendMessage("Team names can only contain letters!").queue()
                return
            }
            if (event.interaction.options[0].asString.length > 16) {
                event.hook.sendMessage("That name is long. Too long. How about 16 characters max?").queue()
                return
            }
            teamManager.addTeam(event.interaction.options[0].asString, event.interaction.user)
            event.hook.sendMessage(":tada: Team **${event.interaction.options[0].asString}** has been created!").queue()
            fileManager.save()
        }
        if (event.interaction.name == "invite") {
            if (clueManager.started) {
                event.hook.sendMessage("The event has started already!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.hook.sendMessage("You are not in a team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user)!!.leader != event.interaction.user) {
                event.hook.sendMessage("You are not the leader of your team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.options[0].asUser) != null) {
                event.hook.sendMessage("That user is already in a team!").queue()
                return
            }
            event.hook.sendMessage("${event.interaction.options[0].asUser.asMention} you have been invited to join **${teamManager.getTeam(event.interaction.user)!!.name}**!")
                .addActionRow(Button.success("accept", "Accept"))
                .queue()
            map[event.interaction.options[0].asUser] = teamManager.getTeam(event.interaction.user)!!
        }
        if (event.interaction.name == "teamleave") {
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.hook.sendMessage("You are not in a team!").queue()
                return
            }
            if (teamManager.getTeam(event.interaction.user)!!.leader == event.interaction.user) {
                val team = teamManager.getTeam(event.interaction.user)!!
                event.hook.sendMessage("You are the leader of this team, and it has been disbanded!").queue()
                team.members.forEach {
                    event.interaction.channel.sendMessage(
                        "${it.asMention} you have been removed from **${team.name}**!"
                    ).queue()
                }
                team.clearMembers()
                teamManager.teams.remove(team)
                team.textChannel.delete().queue()
                team.voiceChannel.delete().queue()
                return
            } else {
                event.hook.sendMessage("You have left **${teamManager.getTeam(event.interaction.user)!!.name}**!").queue()
                teamManager.getTeam(event.interaction.user)!!.removeMember(event.interaction.user)
            }
            fileManager.save()
        }
        if (event.interaction.name == "teamlist") {
            if (teamManager.getTeam(event.interaction.user) == null) {
                event.hook.sendMessage("You are not in a team!").queue()
                return
            }
            event.hook.sendMessage("Members of **${teamManager.getTeam(event.interaction.user)!!.name}**:\n • " +
                    teamManager.getTeam(event.interaction.user)!!.members.joinToString("\n • ") { it.name }).queue()
        }
        if (event.interaction.name == "allteamlist") {
            event.hook.sendMessage("All teams:\n • " +
                    teamManager.teams.joinToString("\n • ") { it.name }).queue()
        }
        if (event.interaction.name == "pushteams") {
            for (team in teamManager.teams) {
                redissonClient.getTopic("mchunt").publish("ID:${team.id},${team.name}")
            }
            event.hook.sendMessage("Pushed teams to Redis!").queue()
        }
    }

    private fun isLetters(string: String): Boolean {
        return string.filter { it in 'A'..'Z' || it in 'a'..'z' || it in " "  }.length == string.length
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (!map.containsKey(event.user)) {
            event.reply("That's not valid...").setEphemeral(true).queue()
            return
        }
        if (map[event.interaction.user]!!.members.size >= 3) {
            event.interaction.reply("That team is full!").queue()
            return
        }
        map[event.interaction.user]!!.addMember(event.interaction.user)
        event.reply("${ map[event.interaction.user]!!.leader.asMention}, ${event.user.name} has joined **${map[event.interaction.user]!!.name}**!").queue()
        map.remove(event.interaction.user)
        fileManager.save()
    }
}