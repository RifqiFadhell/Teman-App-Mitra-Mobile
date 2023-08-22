package id.teman.app.mitra.data.dto.transport

import androidx.annotation.Keep
import id.teman.app.mitra.data.dto.restaurant.RestaurantOrderResponseDto
import id.teman.app.mitra.data.dto.user.response.DriverBasicInfoDto
import id.teman.app.mitra.data.dto.user.response.SimpleUserDetailDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TransportDataResponseDto(
    val count: Int? = null,
    val total: Int? = null,
    val page: Int? = null,
    val pageCount: Int? = null,
    val data: List<TransportResponseDto>? = emptyList()
)

@Keep
@Serializable
data class TransportResponseDto(
    val id: String? = null,
    val type: String? = null,
    @SerialName("payment_method")
    val paymentMethod: String? = null,
    @SerialName("pick_up_lat")
    val pickupLatitude: Double? = null,
    @SerialName("pick_up_lng")
    val pickupLongitude: Double? = null,
    @SerialName("pick_up_address")
    val pickAddress: String? = null,
    @SerialName("pick_up_at")
    val pickUpTime: String? = null,
    @SerialName("drop_off_at")
    val dropOffTime: String? = null,
    @SerialName("drop_off_lat")
    val dropOffLatitude: Double? = null,
    @SerialName("drop_off_lng")
    val dropOffLongitude: Double? = null,
    @SerialName("drop_off_address")
    val dropOffAddress: String? = null,
    @SerialName("drop_off_description")
    val dropOffDescription: String? = null,
    val duration: Double? = null,
    val distance: Double? = null,
    val fare: Double? = null,
    val driver_fare: Double? = null,
    val breakdown: List<PriceBreakdown>? = emptyList(),
    val status: String? = null,
    @SerialName("order_status")
    val restaurantOrderStatus: String? = null,
    val note: String? = null,
    @SerialName("receiver_name")
    val receiverName: String? = null,
    @SerialName("receiver_phone")
    val receiverPhone: String? = null,
    @SerialName("package_type")
    val packageType: String? = null,
    @SerialName("package_weight")
    val packageWeight: Double? = null,
    val rejects: List<String>? = null,
    @SerialName("promotion_id")
    val promotionId: String? = null,
    @SerialName("restaurant_id")
    val restaurantId: String? = null,
    val user: SimpleUserDetailDto? = null,
    val driver: DriverBasicInfoDto? = null,
    val items: List<RestaurantOrderResponseDto>? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("restaurant_fare")
    val restaurantFare: Double? = null,
    val rating: Rating? = null
)

@Keep
@Serializable
data class Rating(
    val rate: Double? = null,
    val note: String? = null,
)
@Keep
@Serializable
data class PriceBreakdown(
    val name: String? = null,
    val type: String? = null,
    val value: Double? = null
)