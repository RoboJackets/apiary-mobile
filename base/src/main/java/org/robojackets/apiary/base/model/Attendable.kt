package org.robojackets.apiary.base.model

data class Attendable(
    val id: Int,
    val name: String,
    val description: String,
    val type: AttendableType,
)

enum class AttendableType {
    Event, Team
}
