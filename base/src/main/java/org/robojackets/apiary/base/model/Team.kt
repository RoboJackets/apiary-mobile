package org.robojackets.apiary.base.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamsHolder(
    val teams: List<Team> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Team(
    val id: Int,
    val name: String,
    val attendable: Boolean,
)
