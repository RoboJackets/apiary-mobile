package org.robojackets.apiary.merchandise.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class MerchandiseRepository @Inject constructor(
    val merchandiseApiService: MerchandiseApiService,
) {
    suspend fun listMerchandiseItems() = merchandiseApiService.getMerchandiseItems()
    suspend fun getDistributionStatus(itemId: Int, gtid: Int) = merchandiseApiService.getDistributionStatus(itemId, gtid)
    suspend fun distributeItem(itemId: Int, gtid: Int, providedVia: String) = merchandiseApiService.distributeItem(itemId, gtid, providedVia)
}