package id.teman.app.mitra.domain.model.restaurant

import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.transport.PriceBreakdown
import id.teman.app.mitra.data.dto.transport.TransportResponseDto
import id.teman.app.mitra.domain.model.transport.PaymentType
import id.teman.app.mitra.domain.model.transport.TransportOrderPaymentSpec
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantOrderSpec(
    val id: String,
    val userPhoto: String,
    val userName: String,
    val orderTime: String,
    val productName: String,
    val totalPrice: Double,
    val orderStatus: RestaurantOrderStatus,
    val orderItems: List<RestaurantOrderItemSpec>,
    val paymentSpec: List<TransportOrderPaymentSpec>,
    val restaurantFare: Double,
    val isWalletPayment: Boolean,
    val rating: Int = 0
)

@Serializable
data class RestaurantOrderItemSpec(
    val name: String,
    val price: Double,
    val description: String,
    val promoPrice: Double,
    val photo: String,
    val quantity: String,
    val note: String,
)

fun List<TransportResponseDto>?.toRestaurantOrderListSpec(): List<RestaurantOrderSpec> {
    if (this == null) return emptyList()
    //val breakdown = flatMap { it.breakdown?.map { payment -> payment.toPaymentSpec() }.orEmpty() }
    return map { it.toRestaurantOrderSpec()}
}

fun TransportResponseDto.toRestaurantOrderSpec(): RestaurantOrderSpec {
    val breakdown = breakdown?.map { payment -> payment.toPaymentSpec() }.orEmpty()
    return RestaurantOrderSpec(
        id = id.orEmpty(),
        userName = user?.name.orEmpty(),
        userPhoto = user?.photo?.url.orEmpty(),
        orderTime = items?.firstOrNull()?.orderCreatedTime.orEmpty(),
        totalPrice = restaurantFare.orZero() ,
        productName = items?.firstOrNull()?.product?.name.orEmpty(),
        orderItems = items?.map { item ->
            RestaurantOrderItemSpec(
                name = item.product?.name.orEmpty(),
                price = item.product?.price.orZero(),
                description = item.product?.description.orEmpty(),
                promoPrice = item.product?.promoPrice.orZero(),
                photo = item.product?.productPhoto?.url.orEmpty(),
                quantity = item.quantity.orZero().toString(),
                note = item.note.orEmpty()
            )
        }.orEmpty(),
        paymentSpec = breakdown,
        orderStatus = RestaurantOrderStatus.from(restaurantOrderStatus),
        restaurantFare = restaurantFare.orZero(),
        isWalletPayment = paymentMethod.orEmpty() != "cash"
    )
}

fun PriceBreakdown.toPaymentSpec(): TransportOrderPaymentSpec {
    return TransportOrderPaymentSpec(
        name = name.orEmpty(),
        price = value.orZero(),
        paymentType = PaymentType.from(type)
    )
}