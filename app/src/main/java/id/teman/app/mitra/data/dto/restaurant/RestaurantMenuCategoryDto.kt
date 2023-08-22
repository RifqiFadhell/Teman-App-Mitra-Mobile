package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RestaurantMenuCategoryDto(
    val name: String? = null,
    val description: String? = null,
    val id: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)