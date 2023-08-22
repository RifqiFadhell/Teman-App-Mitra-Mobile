package id.teman.app.mitra.ui.food.menu.domain

import id.teman.app.mitra.data.dto.restaurant.RestaurantMenuDto
import id.teman.app.mitra.domain.model.restaurant.RestaurantMenuSpec
import id.teman.app.mitra.domain.model.restaurant.toRestaurantMenuSpec
import kotlinx.serialization.Serializable

@Serializable
data class MenuSpec(
    val categoryId: String,
    val menuGroupName: String,
    val totalMenu: Int,
    val menus: List<RestaurantMenuSpec>
)

fun RestaurantMenuDto.toMenuCategorySpec(): MenuSpec {
    return MenuSpec(
        categoryId = id.orEmpty(),
        menuGroupName = name.orEmpty(),
        totalMenu = products.orEmpty().count(),
        menus = products.orEmpty().map { item -> item.toRestaurantMenuSpec() }
    )
}