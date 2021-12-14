package org.robojackets.apiary.base.repository

import com.skydoves.sandwich.ApiResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.base.model.ServerInfoContainer
import org.robojackets.apiary.base.service.ServerInfoApiService
import javax.inject.Inject

@ActivityRetainedScoped
class ServerInfoRepository @Inject constructor(
    val serverInfoApiService: ServerInfoApiService
) {
    suspend fun getServerInfo(): ApiResponse<ServerInfoContainer> {
        return serverInfoApiService.getServerInfo()
    }
}
