package id.teman.app.mitra.data.dto.user.request

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LocationUpdateRequestDto(
    val lat: Double,
    val lng: Double,
    val orientation: Float
)