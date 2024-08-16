package org.robojackets.apiary.merchandise.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.robojackets.apiary.base.model.BasicUser
import org.robojackets.apiary.base.model.UserRef
import java.util.Date

@Suppress("ConstructorParameterNaming")
@JsonClass(generateAdapter = true)
data class DistributionHolder(
    val merchandise: MerchandiseItem,
    val user: BasicUser,
    val distribution: Distribution,
    @Json(name = "can_distribute")
    val canDistribute: Boolean,
)

@Suppress("ConstructorParameterNaming")
@JsonClass(generateAdapter = true)
data class Distribution(
    val id: Int,
    @Json(name = "provided_by")
    val providedBy: UserRef?,
    @Json(name = "provided_at")
    val providedAt: Date?,
    val size: MerchandiseSize?,
)
