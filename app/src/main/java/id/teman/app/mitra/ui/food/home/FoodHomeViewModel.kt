package id.teman.app.mitra.ui.food.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantSummaryFilter
import id.teman.app.mitra.domain.model.restaurant.RestaurantSummarySpec
import id.teman.app.mitra.domain.model.user.MitraRestaurantInfo
import id.teman.app.mitra.domain.model.user.RestaurantStatus
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.user.toUpdateFieldValue
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.restaurant.RestaurantRepository
import id.teman.app.mitra.repository.user.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@HiltViewModel
class FoodHomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val preference: Preference,
    private val userRepository: UserRepository,
    private val json: Json
) : ViewModel() {

    var uiState by mutableStateOf(FoodHomeUiState())
        private set

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(loading = true)
            userRepository.getUserProfile()
                .catch { exception ->
                    uiState= uiState.copy(loading = false, error = Event(exception.message ?: "telah terjadi kesalahan"))
                }
                .collect {
                    uiState = uiState.copy(loading = false, userInfo = it)
                }
        }
    }

    fun getRestaurantDetail() {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.getRestaurantDetail()
                .catch { exception ->
                    uiState = uiState.copy(loading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    reSaveUserInfo(it)
                    uiState = uiState.copy(restaurantDetail = it)
                    getRestaurantOrders()
                    getRestaurantSummary()
                }
        }
    }

    fun getRestaurantSummary(filter: RestaurantSummaryFilter? = null) {
        uiState = uiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.getRestaurantSummary(filter)
                .catch { exception ->
                    uiState = uiState.copy(loading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    uiState = uiState.copy(loading = false, restaurantSummary = it)
                }
        }
    }


    fun getRestaurantOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.getRestaurantOrderRequest()
                .catch { exception ->
                    uiState = uiState.copy(loading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect {
                    uiState = uiState.copy(loading = false, restaurantOrders = it)
                }
        }
    }

    fun updateRestaurantStatus(status: RestaurantStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(loading = true)
            restaurantRepository.updateRestaurantStatus(status.toUpdateFieldValue())
                .catch { exception ->
                    uiState = uiState.copy(loading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }
                .collect {
                    reSaveUserInfo(it)
                    uiState = uiState.copy(loading = false, restaurantDetail = it)
                }
        }
    }

    private suspend fun reSaveUserInfo(mitraResto: MitraRestaurantInfo) {
        val userRawJson = runBlocking { preference.getUserInfo.first() }
        if (userRawJson.isNotNullOrEmpty()) {
            val userInfo = json.decodeFromString<UserInfo>(userRawJson)
            val newUserInfo = userInfo.copy(restaurantInfo = mitraResto)
            preference.setUserInfo(json.encodeToString(newUserInfo))
        }
    }

    fun getUserInfo(): UserInfo? {
        val userRawJson = runBlocking { preference.getUserInfo.first() }
        return if (userRawJson.isNotNullOrEmpty()) {
            json.decodeFromString<UserInfo>(userRawJson)
        } else null
    }
}

data class FoodHomeUiState(
    val loading: Boolean = false,
    val restaurantDetail: MitraRestaurantInfo? = null,
    val userInfo: UserInfo? = null,
    val restaurantOrders: List<RestaurantOrderSpec> = emptyList(),
    val restaurantSummary: RestaurantSummarySpec? = null,
    val error: Event<String>? = null
)