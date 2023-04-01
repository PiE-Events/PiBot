package com.loudbook.dev.scavangerhunt

import java.io.Serializable

data class SerializedTeam(val voiceChannel: Long, val textChannel: Long, val name: String, val leader: Long, val id: String) : Serializable