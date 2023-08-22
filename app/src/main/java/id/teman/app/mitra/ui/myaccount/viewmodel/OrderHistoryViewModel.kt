package id.teman.app.mitra.ui.myaccount.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.transport.OrderHistoryUISection
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.repository.transport.TransportRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val transportRepository: TransportRepository
) : ViewModel() {

    var uiState by mutableStateOf(OrderHistoryUIState())
     private set
    fun getOrderHistory() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            transportRepository.getOrderHistory()
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, errorMessage = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    uiState = uiState.copy(isLoading = false, items = it.first, transportItems = it.second)
                }
        }
    }

    fun getOrderHistoryDetailSpec(id: String): TransportOrderSpec {
        return uiState.transportItems.single { it.requestId == id }
    }
}

data class OrderHistoryUIState(
    val isLoading: Boolean = false,
    val items: List<OrderHistoryUISection> = emptyList(),
    val transportItems: List<TransportOrderSpec> = emptyList(),
    val errorMessage: Event<String>? = null
)