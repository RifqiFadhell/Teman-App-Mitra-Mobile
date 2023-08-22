package id.teman.app.mitra.data.dto.user.response

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DriverBasicInfoDto(
    val id: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val rating: Double? = null,
    @SerialName("last_movement")
    val lastMovement: String? = null,
    val status: String? = null,
    val type: String? = null,
    val verified: Boolean? = null,
    val vaccine: Boolean? = null,
    val city: String? = null,
    @SerialName("driver_license_number")
    val driverLicenseNumber: String? = null,
    @SerialName("vehicle_brand")
    val vehicleBrand: String? = null,
    @SerialName("vehicle_type")
    val vehicleType: String? = null,
    @SerialName("vehicle_year")
    val vehicleYear: String? = null,
    @SerialName("vehicle_number")
    val vehicleNumber: String? = null,
    @SerialName("vehicle_fuel")
    val vehicleFuel: String? = null,
    @SerialName("driver_photo")
    val driverPhoto: UserPhotoDto? = null,
    val user: SimpleUserDetailDto? = null,
)