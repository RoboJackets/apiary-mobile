package org.robojackets.apiary.auth.network

import org.robojackets.apiary.auth.model.User
import retrofit2.Response
import retrofit2.http.GET

interface UserApiService {
    @GET("/api/v1/user")
    suspend fun getUserInfo(): Response<User>
}