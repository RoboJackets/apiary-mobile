package org.robojackets.apiary.base.service

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.base.model.ServerInfoContainer
import retrofit2.http.GET

interface ServerInfoApiService {
    @GET("/api/v1/info")
    suspend fun getServerInfo(): ApiResponse<ServerInfoContainer>
}
