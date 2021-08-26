package org.robojackets.apiary.auth.network

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.auth.model.User
import retrofit2.http.GET

interface UserApiService {
    @GET("/api/v1/user")
    suspend fun getUserInfo(): ApiResponse<User>
}