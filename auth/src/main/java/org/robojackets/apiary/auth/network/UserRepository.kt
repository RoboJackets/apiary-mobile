package org.robojackets.apiary.auth.network

import com.skydoves.sandwich.ApiResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import org.robojackets.apiary.auth.model.User
import javax.inject.Inject

@ActivityRetainedScoped
class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    suspend fun getLoggedInUserInfo(): ApiResponse<User> {
        return userApiService.getUserInfo()
    }
}
