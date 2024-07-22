package org.robojackets.apiary.merchandise.model

import com.squareup.moshi.JsonClass
import org.robojackets.apiary.base.model.BasicUser
import org.robojackets.apiary.base.model.UserRef

@JsonClass(generateAdapter = true)
data class DistributionHolder(
    val merchandise: MerchandiseItem,
    val user: BasicUser,
    val distribution: Distribution,
    val can_distribute: Boolean,
)

@JsonClass(generateAdapter = true)
data class Distribution(
    val id: Int,
    val provided_by: UserRef,
)
