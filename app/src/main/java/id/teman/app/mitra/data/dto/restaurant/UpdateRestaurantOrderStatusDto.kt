package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateRestaurantOrderStatusDto(
    @SerialName("order_status")
    val status: String
)