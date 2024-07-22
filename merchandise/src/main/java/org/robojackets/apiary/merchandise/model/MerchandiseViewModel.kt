package org.robojackets.apiary.merchandise.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    merchandiseItems,
                    loadingMerchandiseItems,
                    error,
                    selectedItem,
                )
            ) {
                flows ->
                    MerchandiseState(
                        merchandiseItems = flows[0] as List<MerchandiseItem>,
                        loadingMerchandiseItems = flows[1] as Boolean,
                        error = flows[2] as String?,
                        selectedItem = flows[3] as MerchandiseItem?,
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
                Timber.e(this.message, "Could not fetch merchandise items due to an exception")
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
        Timber.d("onbuzzcardtap")
        val selectedItemId = selectedItem.value?.id

        if (selectedItemId == null) { // FIXME
            Timber.d("No merchandise item selected")
            return
        }

        viewModelScope.launch {
            merchandiseRepository.getDistributionStatus(selectedItemId, buzzCardTap.gtid)
                .onSuccess {
                    Timber.d("Successfully fetched distribution status")
                    Timber.d(this.data.toString())
                }
                .onFailure {
                    Timber.e("Failed to fetch distribution status")
                    Timber.e(this.toString())
                }
        }

    }
}

data class MerchandiseState(
    val merchandiseItems: List<MerchandiseItem> = emptyList(),
    val loadingMerchandiseItems: Boolean = false,
    val error: String? = null,
    val selectedItem: MerchandiseItem? = null,
)