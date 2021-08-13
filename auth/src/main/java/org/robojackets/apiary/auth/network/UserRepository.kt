package org.robojackets.apiary.auth.network

import org.robojackets.apiary.auth.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    val userApiService: UserApiService
) {
    suspend fun getLoggedInUserInfo(): User? {
        val response = userApiService.getUserInfo()

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}