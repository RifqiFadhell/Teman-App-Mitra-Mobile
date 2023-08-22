package id.teman.app.mitra.domain.model.restaurant

import id.teman.app.mitra.common.orFalse
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.restaurant.ProductResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenuSpec(
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val id: String,
    val promoPrice: Double,
    val isActive: Boolean,
    val createdAt: String,
    val totalSales: Double,
    val isPromo: Boolean,
    val photoUrl: String,
    val categoryName: String? = ""
)

fun ProductResponseDto.toRestaurantMenuSpec(): RestaurantMenuSpec {
    return RestaurantMenuSpec(
        name = name.orEmpty(),
        description = description.orEmpty(),
        price = price.orZero(),
        categoryId = categoryId.orEmpty(),
        id = id.orEmpty(),
        promoPrice = promoPrice.orZero(),
        isActive = isProductActive.orFalse(),
        createdAt = createdAt.orEmpty(),
        totalSales = totalSales.orZero(),
        isPromo = isPromo.orFalse(),
        photoUrl = productPhoto?.url.orEmpty()
    )
}
