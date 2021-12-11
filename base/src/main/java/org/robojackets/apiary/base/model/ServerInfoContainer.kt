package org.robojackets.apiary.base.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerInfoContainer(
    val info: ServerInfo
)

@JsonClass(generateAdapter = true)
data class ServerInfo(
    val appName: String,
    val appEnv: String,
)
