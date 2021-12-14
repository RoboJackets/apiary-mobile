package org.robojackets.apiary.base.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventsHolder(
    val events: List<Event> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EventHolder(
    val event: Event
)

@JsonClass(generateAdapter = true)
data class Event(
    val id: Int,
    val name: String,
//    val startTime: Instant,
//    val endTime: Instant
) {
    fun toAttendable(): Attendable {
        return Attendable(
            id,
            name,
            "",
            AttendableType.Event,
        )
    }
}
