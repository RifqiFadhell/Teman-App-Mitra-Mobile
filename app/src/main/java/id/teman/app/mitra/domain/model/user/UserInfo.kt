package id.teman.app.mitra.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val userId: String,
    val email: String,
    val name: String,
    val point: Double? = 0.0,
    val phoneNumber: String,
    val isVerified: Boolean,
    val minimumBalance: Double? = 0.0,
    val driverInfo: DriverInfo? = null,
    val restaurantInfo: MitraRestaurantInfo? = null,
    val userKycStatus: UserKycStatus = UserKycStatus.UNPROCESSED,
    val isPinAlreadySet: Boolean = false,
    val referralCode: String? = ""
)

@Serializable
data class DriverInfo(
    val id: String,
    val currentLatitude: Double,
    val currentLongitude: Double,
    val rating: Double,
    val status: DriverStatus,
    val isVerified: Boolean,
    val isVaccine: Boolean,
    val city: String,
    val mitraType: DriverMitraType,
    val vehicleNumber: String,
    val vehicleBrand: String,
    val vehicleYear: String,
    val vehicleType: String,
    val vehicleFuel: String,
    val photo: String
)

@Serializable
data class MitraRestaurantInfo(
    val id: String,
    val name: String,
    val description: String,
    val userTypedAddress: String,
    val completedAddress: String,
    val phoneNumber: String,
    val email: String,
    val restaurantLatitude: Double,
    val restaurantLongitude: Double,
    val restaurantOrderStatus: RestaurantStatus,
    val rating: Int,
    val isVerified: Boolean,
    val restaurantPhoto: String,
    val restaurantPhotoId: String,
    val totalOrder: Int,
    val totalIncome: Double
)

@Serializable
enum class DriverStatus(val statusName: String) {
    OFFLINE("offline"),
    ONLINE("online"),
    RIDING("riding"),
    TAKEN("taken");

    companion object {
        fun from(value: String?): DriverStatus = when (value) {
            ONLINE.statusName -> ONLINE
            OFFLINE.statusName -> OFFLINE
            RIDING.statusName -> RIDING
            TAKEN.statusName -> TAKEN
            else -> OFFLINE
        }
    }
}

fun DriverStatus?.isActiveMode() = when (this) {
    DriverStatus.ONLINE,
    DriverStatus.TAKEN,
    DriverStatus.RIDING -> true
    else -> false
}

@Serializable
enum class UserKycStatus(val status: String) {
    UNPROCESSED("unprocessed"),
    REQUESTING("requesting"),
    REJECTED("rejected"),
    APPROVED("approved");

    companion object {
        fun from(value: String?) = when(value) {
            UNPROCESSED.status -> UNPROCESSED
            REQUESTING.status -> REQUESTING
            REJECTED.status -> REJECTED
            APPROVED.status -> APPROVED
            else -> UNPROCESSED
        }
    }
}

@Serializable
enum class DriverMitraType(val type: String) {
    CAR("car"),
    BIKE("bike"),
    FOOD("food"),
    SEND("send"),
    UNKNOWN("Unknown");

    companion object  {
        fun from(value: String?) = when (value) {
            CAR.type -> CAR
            BIKE.type -> BIKE
            FOOD.type -> FOOD
            SEND.type -> SEND
            else -> UNKNOWN
        }
    }
}

fun DriverMitraType.getVehicleType() = when (this) {
    DriverMitraType.CAR -> "car"
    DriverMitraType.BIKE -> "motorcycle"
    DriverMitraType.FOOD -> "motorcycle"
    DriverMitraType.UNKNOWN -> "motorcycle"
    DriverMitraType.SEND -> "motorcycle"
}