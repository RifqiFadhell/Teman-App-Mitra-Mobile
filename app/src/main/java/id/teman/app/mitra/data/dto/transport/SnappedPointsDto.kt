package id.teman.app.mitra.data.dto.transport

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SnappedPointsDto(
    val snappedPoints: List<SnappedLocationDto>? = null
)

@Keep
@Serializable
data class SnappedLocationDto(
    val location: SnappedLocationDetailDto? = null
)

@Keep
@Serializable
data class SnappedLocationDetailDto(
    val latitude: Double? = null,
    val longitude: Double? = null
)