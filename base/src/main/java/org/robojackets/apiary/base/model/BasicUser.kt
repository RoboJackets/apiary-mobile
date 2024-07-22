package org.robojackets.apiary.base.model

import com.squareup.moshi.JsonClass

@Suppress("ConstructorParameterNaming")
@JsonClass(generateAdapter = true)
data class BasicUser(
    val id: Int,
    val uid: String,
    val name: String,
    val preferred_first_name: String,
)

@JsonClass(generateAdapter = true)
data class UserRef(
    val id: Int,
    val full_name: String,
)