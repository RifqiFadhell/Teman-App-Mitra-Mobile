package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ActiveCategoriesRequestDto(
    @SerialName("products_is_active")
    val isProductActive: Boolean
)
