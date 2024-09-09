package org.robojackets.apiary.base.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

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
    @Json(name = "start_time")
    val startTime: Date?,
    @Json(name = "end_time")
    val endTime: Date?
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
