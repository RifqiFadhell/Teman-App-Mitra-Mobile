package id.teman.app.mitra.ui.myaccount.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.restaurant.OpenHoursSpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantOpenHourSpec
import id.teman.app.mitra.repository.restaurant.RestaurantRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class RestaurantOpenHoursViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
): ViewModel() {

    var uiSpec by mutableStateOf(RestaurantOpenHoursUiState())
        private set

    fun getOpenHours() {
        uiSpec = uiSpec.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.getRestaurantHours()
                .catch { exception ->
                    uiSpec = uiSpec.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    uiSpec = uiSpec.copy(isLoading = false, restaurantHours = it)
                }
        }
    }

    fun saveChangesHours() {
        uiSpec = uiSpec.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO){
            restaurantRepository.updateRestaurantHours(uiSpec.restaurantHours)
                .catch { exception ->
                    uiSpec = uiSpec.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }
                .collect {
                    uiSpec = uiSpec.copy(isLoading = false, restaurantHours = it, successUpdateRestaurantHours = Event(Unit))
                }
        }
    }

    fun updateOpenHours(itemIndex: Int, timeIndex: Int, startTime: String) {
        val newList = uiSpec.restaurantHours.toMutableList()

        val openHoursList = newList[itemIndex].openHours.toMutableList()
        openHoursList[timeIndex] = openHoursList[timeIndex].copy(startTime = startTime)

        newList[itemIndex] = newList[itemIndex].copy(
            openHours = openHoursList
        )
        uiSpec = uiSpec.copy(restaurantHours = newList)
    }

    fun updateIsOpen(index: Int, isOpen: Boolean) {
        val oldList = uiSpec.restaurantHours.toMutableList()
        val updatedHours = oldList[index].copy(isOpenForTheDay = isOpen)
        oldList[index] = updatedHours
        uiSpec = uiSpec.copy(restaurantHours = oldList)
    }

    fun update24Hours(index: Int, isOpen24Hours: Boolean) {
        val oldList = uiSpec.restaurantHours.toMutableList()
        val updatedHours = oldList[index].copy(isOpen24Hour = isOpen24Hours)
        oldList[index] = updatedHours
        uiSpec = uiSpec.copy(restaurantHours = oldList)
    }

    fun updateCloseHours(itemIndex: Int, timeIndex: Int, closeTime: String) {
        val newList = uiSpec.restaurantHours.toMutableList()

        // check if item index is not more than item size
        if (itemIndex < 0 && itemIndex > newList.count()) return
        // check if time index is not more than time index size
        if (timeIndex < 0 || timeIndex >= newList[itemIndex].openHours.count()) return

        val openHoursList = newList[itemIndex].openHours.toMutableList()
        openHoursList[timeIndex] = openHoursList[timeIndex].copy(endTime = closeTime)

        newList[itemIndex] = newList[itemIndex].copy(
            openHours = openHoursList
        )
        uiSpec = uiSpec.copy(restaurantHours = newList)
    }

    fun updateRemoveAdditionalTime(itemIndex: Int, timeIndex: Int) {
        val newList = uiSpec.restaurantHours.toMutableList()

        if (itemIndex < 0 && itemIndex > newList.count()) return
        if (timeIndex < 0 || timeIndex >= newList[itemIndex].openHours.count()) return

        val openHours = newList[itemIndex].openHours.toMutableList()
        openHours.removeAt(timeIndex)
        newList[itemIndex] = newList[itemIndex].copy(openHours = openHours)

        uiSpec = uiSpec.copy(restaurantHours = newList)
    }

    fun addAdditionalTime(itemIndex: Int) {
        val newList = uiSpec.restaurantHours.toMutableList()

        if (itemIndex < 0 && itemIndex > newList.count()) return

        val openHours = newList[itemIndex].openHours.toMutableList()
        openHours.add(OpenHoursSpec("12:00", "16:00"))
        newList[itemIndex] = newList[itemIndex].copy(openHours = openHours)
        uiSpec = uiSpec.copy(restaurantHours = newList)
    }
}

data class RestaurantOpenHoursUiState(
    val isLoading: Boolean = false,
    val restaurantHours: List<RestaurantOpenHourSpec> = emptyList(),
    val successUpdateRestaurantHours: Event<Unit>? = null,
    val error: Event<String>? = null
)