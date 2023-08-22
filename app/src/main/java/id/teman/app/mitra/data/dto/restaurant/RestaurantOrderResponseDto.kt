package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RestaurantOrderResponseDto(
    val id: String? = null,
    val note: String? = null,
    val quantity: Int? = null,
    val price: Double? = null,
    val product: ProductResponseDto? = null,
    @SerialName("created_at")
    val orderCreatedTime: String,
    @SerialName("request_id")
    val requestId: String? = null,
    @SerialName("product_id")
    val productId: String? = null,
)