package org.robojackets.apiary.base.service

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.base.model.EventHolder
import org.robojackets.apiary.base.model.EventsHolder
import org.robojackets.apiary.base.model.TeamHolder
import org.robojackets.apiary.base.model.TeamsHolder
import retrofit2.http.GET
import retrofit2.http.Path

interface MeetingsService {
    @GET("/api/v1/teams")
    suspend fun getTeams(): ApiResponse<TeamsHolder>

    @GET("/api/v1/teams/{teamId}")
    suspend fun getTeam(
        @Path("teamId") teamId: Int,
    ): ApiResponse<TeamHolder>

    @GET("/api/v1/events")
    suspend fun getEvents(): ApiResponse<EventsHolder>

    @GET("/api/v1/events/{eventId}")
    suspend fun getEvent(
        @Path("eventId") eventId: Int,
    ): ApiResponse<EventHolder>
}