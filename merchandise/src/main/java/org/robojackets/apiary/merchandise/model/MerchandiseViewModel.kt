package org.robojackets.apiary.merchandise.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.robojackets.apiary.base.model.ApiErrorMessage
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap
import org.robojackets.apiary.base.ui.snackbar.SnackbarController
import org.robojackets.apiary.merchandise.network.MerchandiseRepository
import org.robojackets.apiary.navigation.NavigationActions
import org.robojackets.apiary.navigation.NavigationManager
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MerchandiseViewModel @Inject constructor(
    val merchandiseRepository: MerchandiseRepository,
    val navManager: NavigationManager,
) : ViewModel() {
    private val _state = MutableStateFlow(MerchandiseState())
    val state: StateFlow<MerchandiseState>
        get() = _state

    private val merchandiseItems = MutableStateFlow<List<MerchandiseItem>?>(null)
    private val loadingMerchandiseItems = MutableStateFlow(false)
    private val merchandiseItemsListError = MutableStateFlow<String?>(null)
    private val error = MutableStateFlow<String?>(null)
    private val selectedItem = MutableStateFlow<MerchandiseItem?>(null)
    private val screenState = MutableStateFlow(MerchandiseDistributionScreenState.ReadyForTap)
    private val lastDistributionStatus: MutableStateFlow<DistributionHolder?> =
        MutableStateFlow(null)
    private val lastAcceptedBuzzCardTap: MutableStateFlow<BuzzCardTap?> = MutableStateFlow(null)
    private val lastStorePickupStatus: MutableStateFlow<StorePickupStatus?> = MutableStateFlow(null)

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
                    lastStorePickupStatus,
                    merchandiseItemsListError,
                )
            ) { flows ->
                MerchandiseState(
                    merchandiseItems = flows[0] as List<MerchandiseItem>?,
                    loadingMerchandiseItems = flows[1] as Boolean,
                    error = flows[2] as String?,
                    selectedItem = flows[3] as MerchandiseItem?,
                    screenState = flows[4] as MerchandiseDistributionScreenState,
                    lastDistributionStatus = flows[5] as DistributionHolder?,
                    lastAcceptedBuzzCardTap = flows[6] as BuzzCardTap?,
                    lastStorePickupStatus = flows[7] as StorePickupStatus?,
                    merchandiseItemsListError = flows[8] as String?,
                )
            }
                .catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }
    }

    fun loadMerchandiseItems(
        forceRefresh: Boolean = false,
        selectedItemId: Int? = null,
    ) {
        merchandiseItemsListError.value = null
        if (merchandiseItems.value?.isNotEmpty() == true && !forceRefresh) {
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
                merchandiseItemsListError.value = "Failed to load merchandise items"
                loadingMerchandiseItems.value = false
            }.onException {
                Timber.e(this.throwable, "Could not fetch merchandise items due to an exception")
                merchandiseItemsListError.value = "Failed to load merchandise items"
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
        val item = merchandiseItems.value?.find { it.id == merchandiseItemId }
        if (item != null) {
            selectedItem.value = item
        } else {
            error.value = "Could not find merchandise item with ID $merchandiseItemId"
            Timber.e("Could not find merchandise item with ID $merchandiseItemId")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun onBuzzCardTap(buzzCardTap: BuzzCardTap) {
        if (screenState.value != MerchandiseDistributionScreenState.ReadyForTap) {
            Timber.d("onBuzzCardTap: Screen state is not ready for tap, ignoring")
            return
        }

        screenState.value = MerchandiseDistributionScreenState.LoadingDistributionStatus

        val selectedItemId = selectedItem.value?.id

        if (selectedItemId == null) {
            error.value = "No merchandise item selected"
            Timber.e("onBuzzCardTap called with no merchandise item selected")
            return
        }

        error.value = null
        lastStorePickupStatus.value = null
        lastDistributionStatus.value = null
        lastAcceptedBuzzCardTap.value = buzzCardTap

        viewModelScope.launch {
            merchandiseRepository.getDistributionStatus(selectedItemId, buzzCardTap.gtid)
                .onSuccess {
                    Timber.d("Successfully fetched distribution status")
                    Timber.d(this.data.toString())
                    lastDistributionStatus.value = this.data
                    screenState.value = MerchandiseDistributionScreenState.ShowPickupStatusDialog
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
                        errorModel =
                            this.deserializeErrorBody<DistributionHolder, ApiErrorMessage>()
                    } catch (e: Exception) {
                        Timber.e(e, "Could not deserialize error body")
                    }
                    Timber.d("status: ${errorModel?.status}, message: ${errorModel?.message}")
                    screenState.value = MerchandiseDistributionScreenState.ShowPickupStatusDialog

                    error.value = errorModel?.message ?: "Failed to fetch distribution status"
                }.onException {
                    Timber.e(this.throwable, "Failed to fetch distribution status due to an exception")
                    error.value = "Failed to fetch distribution status"
                    screenState.value = MerchandiseDistributionScreenState.ShowPickupStatusDialog
                }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun confirmPickup() {
        screenState.value = MerchandiseDistributionScreenState.SavingPickupStatus
        viewModelScope.launch {
            val selectedItem = selectedItem.value
            val lastAcceptedBuzzCardTap = lastAcceptedBuzzCardTap.value

            if (selectedItem == null) {
                error.value = "No merchandise item selected"
                Timber.e("No merchandise item selected")
                return@launch
            }

            if (lastAcceptedBuzzCardTap == null) {
                error.value = "BuzzCard data for pickup was not found"
                Timber.e("Last BuzzCardTap is null")
                return@launch
            }

            merchandiseRepository.distributeItem(
                itemId = selectedItem.id,
                gtid = lastAcceptedBuzzCardTap.gtid,
                providedVia = "MyRoboJackets Android - ${lastAcceptedBuzzCardTap.source}"
            ).onSuccess {
                screenState.value = MerchandiseDistributionScreenState.ReadyForTap
                lastStorePickupStatus.value = StorePickupStatus(
                    error = null,
                    user = this.data.user
                )
                SnackbarController.showMessage("Saved pickup for ${this.data.user.name}")
            }.onError {
                // `this.errorBody` can only be consumed once. If you add a log statement
                // including it, then the deserializeErrorBody call will fail
                var errorModel: ApiErrorMessage? = null
                try {
                    errorModel = this.deserializeErrorBody<DistributionHolder, ApiErrorMessage>()
                } catch (e: Exception) {
                    Timber.e(e, "Could not deserialize error body")
                }
                Timber.d("status: ${errorModel?.status}, message: ${errorModel?.message}")
                error.value = errorModel?.message ?: "Error recording merchandise distribution"
                screenState.value = MerchandiseDistributionScreenState.ShowDistributionErrorDialog
            }.onException {
                Timber.e(this.throwable, "Unable to record merchandise distribution due to an exception")
                error.value = "Error recording merchandise distribution"
                screenState.value = MerchandiseDistributionScreenState.ShowDistributionErrorDialog
            }
        }
    }

    fun dismissPickupDialog() {
        screenState.value = MerchandiseDistributionScreenState.ReadyForTap
    }
}

data class MerchandiseState(
    val merchandiseItems: List<MerchandiseItem>? = null,
    val loadingMerchandiseItems: Boolean = false,
    val error: String? = null,
    val selectedItem: MerchandiseItem? = null,
    val screenState: MerchandiseDistributionScreenState = MerchandiseDistributionScreenState.ReadyForTap,
    val lastDistributionStatus: DistributionHolder? = null,
    val lastAcceptedBuzzCardTap: BuzzCardTap? = null,
    val lastStorePickupStatus: StorePickupStatus? = null,
    val merchandiseItemsListError: String? = null,
)
