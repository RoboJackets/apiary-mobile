package org.robojackets.apiary.base.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorMessage(
    val status: String?,
    val message: String?,
)
