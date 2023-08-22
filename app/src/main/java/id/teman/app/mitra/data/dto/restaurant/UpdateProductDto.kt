package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateProductDto(
    val name: String,
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("is_active")
    val isActive: Boolean,
)