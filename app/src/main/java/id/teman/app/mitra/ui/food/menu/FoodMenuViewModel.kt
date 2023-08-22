package id.teman.app.mitra.ui.food.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.restaurant.RestaurantMenuSpec
import id.teman.app.mitra.repository.restaurant.RestaurantRepository
import id.teman.app.mitra.ui.food.menu.domain.MenuFilter
import id.teman.app.mitra.ui.food.menu.domain.MenuSpec
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class FoodMenuViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    var uiState by mutableStateOf(RestaurantUiState())
        private set

    val menuCategories = mutableStateListOf<MenuSpec>()

    fun getMenuCategories(isActive: Boolean? = null) {
        menuCategories.clear()
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.getRestaurantMenuCategory(isActive)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }
                .collect {
                    menuCategories.addAll(it)
                    uiState = uiState.copy(isLoading = false, menuCategories = it)
                }
        }
    }

    fun updateMenuCategory(newName: String, item: MenuSpec, isActive: Boolean? = null) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.updateRestaurantMenuCategory(item.categoryId, newName)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }.collect { spec ->
                    uiState = uiState.copy(updateMenuSuccess = Event(Unit))
                    getMenuCategories(isActive)
                }
        }
    }

    fun updateProductStatus(item: RestaurantMenuSpec, isStatusActive: Boolean) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            restaurantRepository.updateProduct(item, isStatusActive)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }
                .collect { newRestaurantProduct ->
                    /* no-op */
                    uiState = uiState.copy(isLoading = false)
                }
        }
    }

    fun filterCategoriesByStatus(filter: MenuFilter) {
        uiState = when (filter) {
            MenuFilter.ALL -> uiState.copy(menuCategories = menuCategories)
            MenuFilter.NOT_AVAILABLE -> {
                val categories = menuCategories.map { spec ->
                    val menu = spec.menus.filter { it.isActive.not() }
                    spec.copy(menus = menu, totalMenu = menu.count())
                }
                uiState.copy(menuCategories = categories)
            }
            MenuFilter.AVAILABLE -> {
                val categories = menuCategories.map { spec ->
                    val menu = spec.menus.filter { it.isActive }
                    spec.copy(menus = menu, totalMenu = menu.count())
                }
                uiState.copy(menuCategories = categories)
            }
        }
    }
}

data class RestaurantUiState(
    val isLoading: Boolean = false,
    val menuCategories: List<MenuSpec> = emptyList(),
    val filter: MenuFilter = MenuFilter.ALL,
    val updateMenuSuccess: Event<Unit>? = null,
    val error: Event<String>? = null
)