package id.teman.app.mitra.data.dto.user.response

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MitraRestaurantBasicInfoDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val address: String? = null,
    @SerialName("optional_address")
    val nearbyHintAddress: String? = null,
    @SerialName("postal_code")
    val postalCode: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val status: String? = null,
    val rating: Int? = null,
    val verified: Boolean? = null,
    @SerialName("total_sales")
    val totalSales: Int? = null,
    @SerialName("total_rating")
    val totalRating: Double? = null,
    @SerialName("is_promo")
    val isPromo: Boolean? = null,
    val categories: List<String>? = null,
    @SerialName("restaurant_photo")
    val restaurantPhoto: BasicPhotoResponseDto? = null
)

@Keep
@Serializable
data class BasicPhotoResponseDto(
    val url: String? = null,
    val id: String? = null
)