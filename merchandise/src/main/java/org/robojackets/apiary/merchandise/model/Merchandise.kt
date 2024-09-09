package org.robojackets.apiary.merchandise.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchandiseItemsHolder(
    val merchandise: List<MerchandiseItem>
)

@JsonClass(generateAdapter = true)
data class MerchandiseItemHolder(
    val merchandise: MerchandiseItem
)

@JsonClass(generateAdapter = true)
data class MerchandiseItem(
    val id: Int,
    val name: String,
    val distributable: Boolean,
)
