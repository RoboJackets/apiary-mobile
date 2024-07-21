package org.robojackets.apiary.merchandise.network

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.merchandise.model.MerchandiseItemsHolder
import retrofit2.http.GET

interface MerchandiseApiService {
    @GET("/api/v1/merchandise")
    suspend fun getMerchandiseItems(): ApiResponse<MerchandiseItemsHolder>
}