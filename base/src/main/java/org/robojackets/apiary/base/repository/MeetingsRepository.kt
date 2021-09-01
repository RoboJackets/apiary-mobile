package org.robojackets.apiary.base.repository

import com.skydoves.sandwich.ApiResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.base.model.EventsHolder
import org.robojackets.apiary.base.model.TeamsHolder
import org.robojackets.apiary.base.service.MeetingsService
import javax.inject.Inject

@ActivityRetainedScoped
class MeetingsRepository @Inject constructor(
    val meetingsService: MeetingsService
) {
    suspend fun getTeams(): ApiResponse<TeamsHolder> {
        return meetingsService.getTeams()
    }

    suspend fun getEvents(): ApiResponse<EventsHolder> {
        return meetingsService.getEvents()
    }
}