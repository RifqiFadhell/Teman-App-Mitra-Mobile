package id.teman.app.mitra.domain.model.transport

import id.teman.app.mitra.common.decimalFormat
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.transport.TransportResponseDto
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderStatus
import id.teman.app.mitra.domain.model.user.DriverMitraType
import id.teman.app.mitra.domain.model.user.DriverStatus
import kotlin.math.roundToInt
import kotlinx.serialization.Serializable

@Serializable
data class TransportOrderSpec(
    val requestId: String,
    val driverPhoto: String,
    val driverName: String,
    val driverStatus: DriverStatus,
    val driverType: DriverMitraType,
    val paymentMethod: String,
    val totalPrice: Double,
    val totalPriceResto: Double,
    val distance: Double,
    val duration: Int,
    val pickUpTime: String,
    val dropOffTime: String,
    val pickupAddress: String,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val destinationAddress: String,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val driverIncome: Double,
    val orderStatus: TransportRequestType,
    val paymentBreakdown: List<TransportOrderPaymentSpec>,
    val distanceText: String = "",
    val durationText: String = "",
    val notes: String,
    val receiverName: String,
    val receiverNumber: String,
    val packageWeight: String,
    val packageType: String,
    val customerPhoneNumber: String,
    val customerName: String,
    val rating: Int? = null,
    val notesRating: String? = null,
    val itemsFood: List<TransportOrderPaymentSpec> = emptyList(),
    val restaurantOrderStatus: RestaurantOrderStatus
)

fun TransportOrderSpec.isTerminalStatus() =
    this.orderStatus == TransportRequestType.ARRIVED || this.orderStatus == TransportRequestType.FINISHED
            || this.orderStatus == TransportRequestType.REJECTED

@Serializable
data class TransportOrderPaymentSpec(
    val name: String,
    val price: Double,
    val paymentType: PaymentType,
    val quantity: String = "",
    val notes: String = "",
)

enum class PaymentType(val value: String) {
    FOOD("food"),
    FARE("fare"),
    SAFETY("safety"),
    ADMIN("admin"),
    DISCOUNT("discount");

    companion object {
        fun from(value: String?) = when (value) {
            FOOD.value -> FOOD
            FARE.value -> FARE
            SAFETY.value -> SAFETY
            ADMIN.value -> ADMIN
            else -> DISCOUNT
        }
    }
}

fun List<TransportResponseDto>.toTransportOrderSpecList(): List<TransportOrderSpec> {
    return map { it.toTransportOrderSpec() }
}

fun TransportResponseDto.toTransportOrderSpec(): TransportOrderSpec {
    val priceBreakdown = breakdown?.map { priceBreakdown ->
        TransportOrderPaymentSpec(
            name = priceBreakdown.name.orEmpty(),
            price = priceBreakdown.value.orZero(),
            paymentType = PaymentType.from(priceBreakdown.type)
        )
    }.orEmpty()

    val foodPrice = ArrayList<TransportOrderPaymentSpec>()
    for (x in 0 until items?.size.orZero()) {
        if (items?.get(x)?.quantity.orZero() != 0) {
            foodPrice.add(TransportOrderPaymentSpec(
                name = items?.get(x)?.product?.name.orEmpty(),
                price = items?.get(x)?.price.orZero(),
                paymentType = PaymentType.FOOD,
                quantity = items?.get(x)?.quantity.toString(),
                notes = items?.get(x)?.note.orEmpty()
            ))
        }
    }
    return TransportOrderSpec(
        requestId = id.orEmpty(),
        driverName = driver?.user?.name.orEmpty(),
        driverPhoto = driver?.driverPhoto?.url.orEmpty(),
        driverStatus = DriverStatus.from(driver?.status),
        driverType = DriverMitraType.from(type),
        paymentMethod = paymentMethod.orEmpty(),
        totalPrice = fare.orZero(),
        duration = duration?.toInt() ?: 0,
        distance = distance.orZero(),
        pickUpTime = pickUpTime.orEmpty(),
        dropOffTime = dropOffTime.orEmpty(),
        pickupAddress = pickAddress.orEmpty(),
        pickupLatitude = pickupLatitude.orZero(),
        pickupLongitude = pickupLongitude.orZero(),
        destinationAddress = dropOffAddress.orEmpty(),
        destinationLatitude = dropOffLatitude.orZero(),
        destinationLongitude = dropOffLongitude.orZero(),
        driverIncome = driver_fare.orZero(),
        orderStatus = TransportRequestType.from(status),
        paymentBreakdown = priceBreakdown,
        notes = note.orEmpty(),
        receiverName = receiverName.orEmpty(),
        receiverNumber = receiverPhone.orEmpty(),
        packageWeight = decimalFormat(packageWeight.orZero()),
        packageType = packageType.orEmpty(),
        customerName = user?.name.orEmpty(),
        customerPhoneNumber = user?.phoneNumber.orEmpty(),
        itemsFood = foodPrice,
        rating = rating?.rate.orZero().roundToInt(),
        notesRating = rating?.note.orEmpty(),
        totalPriceResto = restaurantFare.orZero(),
        restaurantOrderStatus = RestaurantOrderStatus.from(restaurantOrderStatus)
    )
}
