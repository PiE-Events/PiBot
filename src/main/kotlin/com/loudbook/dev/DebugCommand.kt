package com.loudbook.dev

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class DebugCommand : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.interaction.name == "debug") {
            event.hook.sendMessage("""
                **Debug Info:**
                - Ping: `${event.jda.gatewayPing}ms`
                - OS Name: `${System.getProperty("os.name")}`
                - OS Version: `${System.getProperty("os.version")}`
                - Device Architecture: `${System.getProperty("os.arch")}`
                - Java Version: `${System.getProperty("java.version")}`
            """.trimIndent()).queue()
        }
    }
}