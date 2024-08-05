package org.robojackets.apiary.merchandise.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchandiseSize(
    val short: String,
    @Json(name = "display_name")
    val displayName: String,
)
