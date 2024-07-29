package org.robojackets.apiary.merchandise.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import com.skydoves.sandwich.retrofit.statusCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.model.ApiErrorMessage
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.merchandise.network.MerchandiseRepository
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MerchandiseViewModel @Inject constructor(
    val merchandiseRepository: MerchandiseRepository,
    val navManager: NavigationManager
) : ViewModel() {
    private val _state = MutableStateFlow(MerchandiseState())
    val state: StateFlow<MerchandiseState>
        get() = _state

    private val merchandiseItems = MutableStateFlow<List<MerchandiseItem>>(emptyList())
    private val loadingMerchandiseItems = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val selectedItem = MutableStateFlow<MerchandiseItem?>(null)
    private val screenState = MutableStateFlow(MerchandiseDistributionScreenState.ReadyForTap)
    private val lastDistributionStatus: MutableStateFlow<DistributionHolder?> = MutableStateFlow(null)
    private val lastAcceptedBuzzCardTap: MutableStateFlow<BuzzCardTap?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    merchandiseItems,
                    loadingMerchandiseItems,
                    error,
                    selectedItem,
                    screenState,
                    lastDistributionStatus,
                    lastAcceptedBuzzCardTap,
                )
            ) {
                flows ->
                    MerchandiseState(
                        merchandiseItems = flows[0] as List<MerchandiseItem>,
                        loadingMerchandiseItems = flows[1] as Boolean,
                        error = flows[2] as String?,
                        selectedItem = flows[3] as MerchandiseItem?,
                        screenState = flows[4] as MerchandiseDistributionScreenState,
                        lastDistributionStatus = flows[5] as DistributionHolder?,
                        lastAcceptedBuzzCardTap = flows[6] as BuzzCardTap?,
                    )
            }
            .catch { throwable -> throw throwable }
            .collect { _state.value = it }
        }
    }

    fun loadMerchandiseItems(
        forceRefresh: Boolean = false,
        selectedItemId: Int? = null
    ) {
        if (merchandiseItems.value.isNotEmpty() && !forceRefresh) {
            Timber.d("Using cached merchandise items")
            selectedItemId?.let { selectItemForDistribution(it) }
            return
        }

        loadingMerchandiseItems.value = true
        viewModelScope.launch {
            merchandiseRepository.listMerchandiseItems().onSuccess {
                merchandiseItems.value = this.data.merchandise
                selectedItemId?.let { selectItemForDistribution(it) }
                loadingMerchandiseItems.value = false
            }.onError {
                Timber.e(this.toString(), "Could not fetch merchandise items due to an error")
                error.value = "Unable to fetch merchandise items"
                loadingMerchandiseItems.value = false
            }.onException {
                Timber.e(this.throwable, "Could not fetch merchandise items due to an exception")
                error.value = "Unable to fetch merchandise items"
                loadingMerchandiseItems.value = false
            }
        }
    }

    fun navigateToMerchandiseItemDistribution(item: MerchandiseItem) {
        navManager.navigate(NavigationActions.Merchandise.merchandiseIndexToDistribution(item.id))
    }

    fun navigateToMerchandiseIndex() {
        navManager.navigate(NavigationActions.Merchandise.merchandiseDistributionToIndex())
    }

    private fun selectItemForDistribution(merchandiseItemId: Int) {
        val item = merchandiseItems.value.find { it.id == merchandiseItemId }
        if (item != null) {
            selectedItem.value = item
        } else {
            error.value = "Could not find merchandise item with ID $merchandiseItemId"
            Timber.e("Could not find merchandise item with ID $merchandiseItemId")
        }
    }

    fun onBuzzCardTap(buzzCardTap: BuzzCardTap) {
        // TODO: What happens if this is called while the screen is in the wrong state?

        Timber.d("onbuzzcardtap")
        val selectedItemId = selectedItem.value?.id

        if (selectedItemId == null) { // FIXME
            Timber.d("No merchandise item selected")
            return
        }

        if (screenState.value != MerchandiseDistributionScreenState.ReadyForTap) {
            Timber.d("Screen state is not ready for tap")
        }

        lastAcceptedBuzzCardTap.value = buzzCardTap
        screenState.value = MerchandiseDistributionScreenState.LoadingDistributionStatus

        viewModelScope.launch {
            merchandiseRepository.getDistributionStatus(selectedItemId, buzzCardTap.gtid)
                .onSuccess {
                    Timber.d("Successfully fetched distribution status")
                    Timber.d(this.data.toString())
                    lastDistributionStatus.value = this.data
                    screenState.value = MerchandiseDistributionScreenState.ShowStatusDialog
                }
                .onError {
                    // `this.errorBody` can only be consumed once. If you add a log statement
                    // including it, then the deserializeErrorBody call will fail
                    var errorModel: ApiErrorMessage? = null
                    try {
                        // Sandwich docs on deserializing errors: https://skydoves.github.io/sandwich/retrofit/#error-body-deserialization
                        // <A, B> where A is the return type of the outer API call (getDistributionStatus)
                        // and B is the type to parse the error body as
                        // If Android Studio is constantly suggesting to import the deserializeErrorBody
                        // method, or you get build errors like "None of the following candidates is
                        // applicable because of receiver type mismatch," it's probably because
                        // you're specifying the wrong type for A
                        errorModel = this.deserializeErrorBody<DistributionHolder, ApiErrorMessage>()
                    } catch (e: Exception) {
                        Timber.e(e, "Could not deserialize error body")
                    }
                    Timber.d("status: ${errorModel?.status}, message: ${errorModel?.message}")
                    screenState.value = MerchandiseDistributionScreenState.ShowStatusDialog

                    error.value = when {
                        this.statusCode == StatusCode.NotFound && errorModel?.status == null -> {
                            "No user found for this GTID"
                        }
                        else -> {
                            errorModel?.message ?: "Failed to fetch distribution status"
                        }
                    }
                }
        }
    }

    fun confirmPickup() {
        // FIXME
        screenState.value = MerchandiseDistributionScreenState.SavingPickupStatus
        viewModelScope.launch {
            // FIXME: nested lets is so gross
            selectedItem.value?.let { itemId ->
                lastAcceptedBuzzCardTap.value?.let { buzzCardTap ->
                    merchandiseRepository.distributeItem(
                        itemId = itemId.id,
                        gtid = buzzCardTap.gtid,
                        providedVia = "MyRoboJackets Android - ${buzzCardTap.source}" // FIXME: figure out how to get tap source here
                    ).onSuccess {
                        screenState.value = MerchandiseDistributionScreenState.ReadyForTap
                    }.onError { // TODO: Reduce code duplication
                        // TODO: If distribution fails, what should we do?
                        // TODO: Implement loading state while saving distribution
                        // `this.errorBody` can only be consumed once. If you add a log statement
                        // including it, then the deserializeErrorBody call will fail
                        var errorModel: ApiErrorMessage? = null
                        try {
                            // Sandwich docs on deserializing errors: https://skydoves.github.io/sandwich/retrofit/#error-body-deserialization
                            // <A, B> where A is the return type of the outer API call (getDistributionStatus)
                            // and B is the type to parse the error body as
                            // If Android Studio is constantly suggesting to import the deserializeErrorBody
                            // method, or you get build errors like "None of the following candidates is
                            // applicable because of receiver type mismatch," it's probably because
                            // you're specifying the wrong type for A
                            errorModel = this.deserializeErrorBody<DistributionHolder, ApiErrorMessage>()
                        } catch (e: Exception) {
                            Timber.e(e, "Could not deserialize error body")
                        }
                        Timber.d("status: ${errorModel?.status}, message: ${errorModel?.message}")
                        screenState.value = MerchandiseDistributionScreenState.ShowStatusDialog

                        error.value = when {
                            this.statusCode == StatusCode.NotFound && errorModel?.status == null -> {
                                "No user found for this GTID"
                            }

                            else -> {
                                errorModel?.message ?: "Failed to fetch distribution status"
                            }
                        }
                    }
                }
            }
        }
    }

    fun dismissPickupDialog() {
        screenState.value = MerchandiseDistributionScreenState.ReadyForTap
    }
}

data class MerchandiseState(
    val merchandiseItems: List<MerchandiseItem> = emptyList(),
    val loadingMerchandiseItems: Boolean = false,
    val error: String? = null,
    val selectedItem: MerchandiseItem? = null,
    val screenState: MerchandiseDistributionScreenState = MerchandiseDistributionScreenState.ReadyForTap,
    val lastDistributionStatus: DistributionHolder? = null,
    val lastAcceptedBuzzCardTap: BuzzCardTap? = null,
)