package org.robojackets.apiary.merchandise.model

import org.robojackets.apiary.base.model.BasicUser

data class StorePickupStatus(
    val error: String?,
    val user: BasicUser,
)
