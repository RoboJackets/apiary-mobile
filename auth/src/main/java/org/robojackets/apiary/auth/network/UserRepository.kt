package org.robojackets.apiary.auth.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.auth.model.User
import javax.inject.Inject


@ActivityRetainedScoped
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