package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RestaurantCategoriesDto(
    val data: List<RestaurantMenuDto>? = null,
    val count: Int? = null,
    val total: Int? = null,
    val page: Int? = null
)

@Keep
@Serializable
data class RestaurantMenuDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val products: List<ProductResponseDto>? = null
)