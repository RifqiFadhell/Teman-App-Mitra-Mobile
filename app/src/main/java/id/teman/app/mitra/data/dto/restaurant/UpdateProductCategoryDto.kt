package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateProductCategoryDto(
    val name: String
)