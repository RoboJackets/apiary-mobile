package org.robojackets.apiary.base.repository

import org.robojackets.apiary.base.model.NetworkResult
import org.robojackets.apiary.base.model.ServerInfoContainer
import org.robojackets.apiary.base.service.ServerInfoApiService
import javax.inject.Inject

class ServerInfoRepository @Inject constructor(
    val serverInfoApiService: ServerInfoApiService
) {
    suspend fun getServerInfo(): NetworkResult<ServerInfoContainer> {
        val response = serverInfoApiService.getServerInfo()

        val body = response.body()
        if (response.isSuccessful && body != null) {
            return NetworkResult.Success(body)
        }

        return NetworkResult.Error(response.message())
    }
}