package id.teman.app.mitra.data.dto.restaurant

import androidx.annotation.Keep
import id.teman.app.mitra.data.dto.user.response.BasicPhotoResponseDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ProductResponseDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    @SerialName("promo_price")
    val promoPrice: Double? = null,
    @SerialName("total_sales")
    val totalSales: Double? = null,
    @SerialName("is_active")
    val isProductActive: Boolean? = null,
    @SerialName("category_id")
    val categoryId: String? = null,
    @SerialName("restaurant_id")
    val restaurantId: String? = null,
    @SerialName("product_photo")
    val productPhoto: BasicPhotoResponseDto? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("is_promo")
    val isPromo: Boolean? = null
)