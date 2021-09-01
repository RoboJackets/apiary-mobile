package org.robojackets.apiary.base.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BasicUser(
    val id : Int,
    val uid : String,
    val name : String,
    val preferred_first_name: String,
)
