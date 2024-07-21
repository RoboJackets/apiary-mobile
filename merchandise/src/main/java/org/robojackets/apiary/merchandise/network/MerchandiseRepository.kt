package org.robojackets.apiary.merchandise.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class MerchandiseRepository @Inject constructor(
    val merchandiseApiService: MerchandiseApiService,
) {
    suspend fun listMerchandiseItems() = merchandiseApiService.getMerchandiseItems()
}