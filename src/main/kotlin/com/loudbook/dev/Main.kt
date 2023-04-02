package com.loudbook.dev

import com.loudbook.dev.scavangerhunt.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.redisson.Redisson
import org.redisson.api.RTopic
import org.redisson.config.Config
import java.io.FileInputStream
import java.io.IOException
import java.util.*


class Main {
    companion object {
        private var token: String? = null
        private var uri: String? = null

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Warn")
            try {
                withContext(Dispatchers.IO) {
                    FileInputStream("./config.properties").use { input ->
                        val prop = Properties()
                        prop.load(input)
                        token = prop.getProperty("token")
                        uri = prop.getProperty("uri")

                    }
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            val config = Config()
            config.useSingleServer()
                .address = uri

            val redisson = Redisson.create(config)

            val discord = Discord()
            discord.connect(token!!)

            val teamManager = TeamManager(discord.jda, redisson)
            val clueManager = ClueManager(discord.jda, teamManager, redisson)
            val clueParser = ClueParser(clueManager, discord.jda)
            val fileManager = FileManager(teamManager, discord.jda, redisson)
            clueParser.run()
            fileManager.load()
            discord.jda.addEventListener(AnswerCommand(clueManager, teamManager))
            discord.jda.addEventListener(StartEvertCommand(teamManager, clueManager))
            discord.jda.addEventListener(ResetEventCommand(teamManager, clueManager, clueParser))
            discord.jda.addEventListener(TeamCommand(teamManager, clueManager, fileManager, redisson))
            discord.jda.addEventListener(DebugCommand())
            launch {
                val topic: RTopic = redisson.getTopic("mcmessage")
                topic.addListener(
                    String::class.java
                ) { _, msg ->
                    if (msg.startsWith("MESSAGE:")) {
                        val newmsg = msg.replace("MESSAGE:", "")
                        val strings = newmsg.split(":")
                        val name = strings[0]
                        val message = strings[1]
                        discord.sendMCEmbed(name, message.substring(1))
                    } else if (msg.startsWith("JOIN:")) {
                        val newmsg = msg.replace("JOIN:", "")
                        discord.sendMCJoinEmbed(newmsg)
                    } else if (msg.startsWith("LEAVE:")) {
                        val newmsg = msg.replace("LEAVE:", "")
                        discord.sendMCLeaveEmbed(newmsg)
                    } else if (msg.startsWith("DEATH:")) {
                        val newmsg = msg.replace("DEATH:", "")
                        val strings = newmsg.split(":")
                        val killed = strings[0]
                        val killer = strings[1]
                        discord.sendMCDeathEmbed(killer, killed)
                    }
                }
                val topic2: RTopic = redisson.getTopic("mchunt")
                topic2.addListener(
                    String::class.java
                ) { _, msg ->
                    if (msg.startsWith("FINISH:")) {
                        val strings = msg.split(":")
                        val teamID = strings[1]
                        val team = teamManager.getTeam(teamID)
                        if (team != null) {
                            println("Team $teamID finished from Minecraft!")
                            clueManager.executeAnswer(team, clueManager.clues.first { it.number == team.clueNumber }, true)
                        } else {
                            println("Team $teamID not found")
                        }
                    }
                }
                topic.publish("test")
            }
            println("PiBot is running!")
            Runtime.getRuntime().addShutdownHook(Thread {
                discord.jda.shutdown()
                redisson.shutdown()
                println("Done here. Bye.")
            })
        }
    }
}


