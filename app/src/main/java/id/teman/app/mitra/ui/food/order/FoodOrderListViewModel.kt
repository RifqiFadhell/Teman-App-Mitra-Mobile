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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class FoodOrderListViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
): ViewModel() {

    var uiState by mutableStateOf(FoodListUiState())
        private set

    private var searchJob: Job? = null

    override fun onCleared() {
        super.onCleared()
        if (searchJob != null) {
            searchJob?.cancel()
        }
    }

    fun searchDebounced(searchText: String, status: RestaurantOrderStatus? = null) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getRestaurantOrders(searchText, status)
        }
    }

    fun getRestaurantOrders(query: String? = null, status: RestaurantOrderStatus? = null) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO){
            restaurantRepository.getRestaurantOrderRequest(status, query)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    uiState = uiState.copy(isLoading = false, restaurantOrders = it)
                }
        }
    }
}

data class FoodListUiState(
    val isLoading: Boolean = false,
    val restaurantOrders: List<RestaurantOrderSpec> = emptyList(),
    val error: Event<String>? = null
)