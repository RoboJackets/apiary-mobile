package org.robojackets.apiary.auth.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val user: UserInfo
)

@JsonClass(generateAdapter = true)
data class UserInfo(
    val id : Int,
    val uid : String,
    val name : String,
    val preferred_first_name: String,
    val allPermissions: List<String>,
)
