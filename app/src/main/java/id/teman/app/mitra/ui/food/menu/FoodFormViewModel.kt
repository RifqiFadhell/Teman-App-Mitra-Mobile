package id.teman.app.mitra.ui.food.menu

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.createMultipartImageFromUriGallery
import id.teman.app.mitra.common.createPartFromString
import id.teman.app.mitra.repository.restaurant.RestaurantRepository
import id.teman.app.mitra.ui.food.menu.domain.MenuSpec
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.RequestBody

@HiltViewModel
class FoodFormViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val application: Application
) : ViewModel() {

    var uiState by mutableStateOf(FoodFormUiState())
        private set

    fun getRestaurantMenuCategories() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.getRestaurantMenuCategory(null)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }
                .collect { spec ->
                    uiState = uiState.copy(isLoading = false, menuCategories = spec)
                }
        }
    }

    fun createRestaurantCategory(category: String, description: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.addRestaurantMenuCategory(category, description)
                .catch { exception ->
                    uiState =
                        uiState.copy(isLoading = false, error = Event(exception.message.orEmpty()))
                }.collect {
                    uiState = uiState.copy(isLoading = false, success = Event(Unit))
                }
        }
    }

    fun updateRestaurantMenu(
        menuId: String, menuImage: Uri? = null, menuName: String, menuDescription: String,
        price: String, promoPrice: String? = null, uriPath: String? = null, categoryId: String
    ) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            map["name"] = createPartFromString(menuName)
            map["description"] = createPartFromString(menuDescription)
            map["price"] = createPartFromString(price)
            map["category_id"] = createPartFromString(categoryId)
            if (promoPrice != null) {
                map["promo_price"] = createPartFromString(promoPrice)
                map["is_promo"] = createPartFromString("true")
            } else {
                map["is_promo"] = createPartFromString("false")
            }
            if (menuImage != null && menuImage != Uri.EMPTY) {
                createMultipartImageFromUriGallery(
                    application,
                    menuImage,
                    "product_photo",
                    uriPath.orEmpty()
                )?.let { imagePart ->
                    restaurantRepository.updateRestaurantMenu(map, imagePart, menuId)
                        .catch { exception ->
                            uiState =
                                uiState.copy(
                                    isLoading = false,
                                    error = Event(exception.message.orEmpty())
                                )
                        }
                        .collect {
                            uiState = uiState.copy(isLoading = false, success = Event(Unit))
                        }
                }
            } else {
                restaurantRepository.updateRestaurantMenu(map, null, menuId)
                    .catch { exception ->
                        uiState =
                            uiState.copy(
                                isLoading = false,
                                error = Event(exception.message.orEmpty())
                            )
                    }
                    .collect {
                        uiState = uiState.copy(isLoading = false, success = Event(Unit))
                    }
            }
        }
    }

    fun deleteRestaurantMenu(menuId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.deleteRestaurantMenu(menuId)
                .catch { exception ->
                    uiState=  uiState.copy(isLoading = false, error = Event(exception.message.orEmpty()))
                }
                .collect {
                    uiState = uiState.copy(isLoading = false, successDelete = Event(Unit))
                }
        }
    }

    fun createRestaurantMenu(
        menuImage: Uri, menuName: String, menuDescription: String,
        price: Double, menuCategory: String, promoPrice: String? = null, uriPath: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            map["name"] = createPartFromString(menuName)
            map["description"] = createPartFromString(menuDescription)
            map["price"] = createPartFromString(price.toString())
            map["category_id"] = createPartFromString(menuCategory)
            if (promoPrice != null) {
                map["promo_price"] = createPartFromString(promoPrice)
                map["is_promo"] = createPartFromString("true")
            } else {
                map["is_promo"] = createPartFromString("false")
            }

            createMultipartImageFromUriGallery(
                application,
                menuImage,
                "product_photo",
                uriPath.orEmpty()
            )?.let { imagePart ->
                restaurantRepository.addRestaurantMenu(map, imagePart)
                    .catch { exception ->
                        uiState =
                            uiState.copy(
                                isLoading = false,
                                error = Event(exception.message.orEmpty())
                            )
                    }
                    .collect {
                        uiState = uiState.copy(isLoading = false, success = Event(Unit))
                    }
            }

        }
    }
}

data class FoodFormUiState(
    val isLoading: Boolean = false,
    val menuCategories: List<MenuSpec> = emptyList(),
    val success: Event<Unit>? = null,
    val successDelete: Event<Unit>? = null,
    val error: Event<String>? = null
)