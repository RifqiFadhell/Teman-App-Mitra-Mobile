package id.teman.app.mitra.ui.food.order

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderStatus
import id.teman.app.mitra.repository.restaurant.RestaurantRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class FoodOrderDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    var uiState by mutableStateOf(FoodOrderDetailUIState())

    fun getRestaurantOrderDetail(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            restaurantRepository.getRestaurantOrderDetail(requestId)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    uiState = uiState.copy(isLoading = false, successGetDetail = it)
                }
        }
    }

    fun updateRestaurantOrderStatus(requestId: String, status: RestaurantOrderStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            restaurantRepository.updateRestaurantOrderStatus(requestId, status.value)
                .catch { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = Event(exception.message.orEmpty())
                    )
                }.collect {
                    getRestaurantOrderDetail(requestId)
                }
        }
    }

}

data class FoodOrderDetailUIState(
    val isLoading: Boolean = false,
    val successGetDetail: RestaurantOrderSpec? = null,
    val failedUpdateRestaurantStatus: Event<String>? = null,
    val error: Event<String>? = null
)