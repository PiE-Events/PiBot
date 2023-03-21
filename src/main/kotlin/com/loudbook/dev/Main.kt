ackage com.loudbook.dev

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
        private var discord: Discord? = null
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
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

            /*            val config = Config()
                        config.useSingleServer()
                            .address = uri*/
//            val redisson = Redisson.create(config)

            discord = Discord(token!!)

            val clueManager = ClueManager()
            val clueParser = ClueParser(clueManager)
            val teamManager = TeamManager()
            clueParser.run()

            Discord.jda!!.addEventListener(AnswerCommand(clueManager))
            Discord.jda!!.addEventListener(ResetEventCommand(teamManager, clueManager, clueParser))
            Discord.jda!!.addEventListener(TeamCommand(teamManager))
            println("Help")

            /*            launch {
                            val topic: RTopic = redisson.getTopic("mcmessage")
                            topic.addListener(
                                String::class.java
                            ) { _, msg ->
                                if (msg.startsWith("MESSAGE:")) {
                                    val newstring = msg.substring(msg.split(":")[0].length)
                                    val name = msg.split(":")[1]
                                    val message = msg.substring(newstring.length + name.length + 2)
                                    discord!!.sendMCEmbed(name, message)
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
                        }*/

            println("PiBot is running!")

            Runtime.getRuntime().addShutdownHook(Thread {
                Discord.jda!!.shutdown()
//                redisson.shutdown()
            })
        }
    }
}
