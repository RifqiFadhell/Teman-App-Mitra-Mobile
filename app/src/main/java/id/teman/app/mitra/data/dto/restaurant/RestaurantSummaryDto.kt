package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RestaurantSummaryDto(
    val transaction: Int? = null,
    val income: Double? = null
)