package org.robojackets.apiary.base.service

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.base.model.EventsHolder
import org.robojackets.apiary.base.model.TeamsHolder
import retrofit2.http.GET

interface MeetingsService {
    @GET("/api/v1/teams")
    suspend fun getTeams(): ApiResponse<TeamsHolder>

    @GET("/api/v1/events")
    suspend fun getEvents(): ApiResponse<EventsHolder>
}