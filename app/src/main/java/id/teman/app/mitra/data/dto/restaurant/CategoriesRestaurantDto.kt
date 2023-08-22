package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CategoriesResponseDto(
    val data: List<CategoriesRestaurantDto>? = emptyList()
)

@Keep
@Serializable
data class CategoriesRestaurantDto(
    val id: String?,
    val name: String?,
    val icon: String?
)
