package org.robojackets.apiary.merchandise.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchandiseItemsHolder(
    val merchandise: List<MerchandiseItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MerchandiseItem(
    val id: Int,
    val name: String,
    // TODO: do the other fields matter?
)
