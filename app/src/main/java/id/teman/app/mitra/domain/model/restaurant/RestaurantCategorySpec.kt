package id.teman.app.mitra.domain.model.restaurant

import id.teman.app.mitra.data.dto.restaurant.RestaurantMenuCategoryDto
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantCategorySpec(
    val name: String,
    val id: String,
    val createdAt: String,
    val description: String
)

fun RestaurantMenuCategoryDto.toRestaurantCategorySpec(): RestaurantCategorySpec {
    return RestaurantCategorySpec(
        name = name.orEmpty(),
        description = description.orEmpty(),
        id = id.orEmpty(),
        createdAt = createdAt.orEmpty()
    )
}