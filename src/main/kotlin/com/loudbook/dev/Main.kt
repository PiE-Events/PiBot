package com.loudbook.dev

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        private var discord: Discord? = null
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            try {
                FileInputStream("./config.properties").use { input ->
                    val prop = Properties()
                    prop.load(input)
                    token = prop.getProperty("token")
                    uri = prop.getProperty("uri")

                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            val config = Config()
            config.useSingleServer()
                .address = uri
            val redisson = Redisson.create(config)

            discord = Discord()
            discord!!.connect(token!!)

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
                        discord!!.sendMCEmbed(name, message.substring(1))
                    } else if (msg.startsWith("JOIN:")) {
                        val newmsg = msg.replace("JOIN:", "")
                        discord!!.sendMCJoinEmbed(newmsg)
                    } else if (msg.startsWith("LEAVE:")) {
                        val newmsg = msg.replace("LEAVE:", "")
                        discord!!.sendMCLeaveEmbed(newmsg)
                    } else if (msg.startsWith("DEATH:")) {
                        val newmsg = msg.replace("DEATH:", "")
                        val strings = newmsg.split(":")
                        val killed = strings[0]
                        val killer = strings[1]
                        discord!!.sendMCDeathEmbed(killer, killed)
                    }
                }
                topic.publish("test")
            }



            Runtime.getRuntime().addShutdownHook(Thread {
                redisson.shutdown()
            })
        }
    }
}


