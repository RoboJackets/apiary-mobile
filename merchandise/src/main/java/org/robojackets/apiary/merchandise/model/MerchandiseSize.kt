package org.robojackets.apiary.merchandise.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchandiseSize(
    val short: String?,
    val display_name: String?,
)
