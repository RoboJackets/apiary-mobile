package org.robojackets.apiary.merchandise.network

import com.skydoves.sandwich.ApiResponse
import org.robojackets.apiary.merchandise.model.DistributionHolder
import org.robojackets.apiary.merchandise.model.MerchandiseItemsHolder
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MerchandiseApiService {
    @GET("/api/v1/merchandise")
    suspend fun getMerchandiseItems(): ApiResponse<MerchandiseItemsHolder>

    @GET("/api/v1/merchandise/{itemId}/distribute/{gtid}")
    suspend fun getDistributionStatus(
        @Path("itemId") itemId: Int,
        @Path("gtid") gtid: Int,
    ): ApiResponse<DistributionHolder>

    @FormUrlEncoded
    @POST("/api/v1/merchandise/{itemId}/distribute/{gtid}")
    suspend fun distributeItem(
        @Path("itemId") itemId: Int,
        @Path("gtid") gtid: Int,
        @Field("provided_via") provided_via: String,
    ): ApiResponse<DistributionHolder>
}
