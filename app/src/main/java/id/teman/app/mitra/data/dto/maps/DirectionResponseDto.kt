package id.teman.app.mitra.data.dto.maps

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DirectionResponseDto(
    val routes: List<RoutesResponseDto>? = null,
    val status: String? = "",
    @SerialName("geocoded_waypoints")
    val geocodedWaypoint: List<GeocodedWayPoint>? = emptyList()
)
@Keep
@Serializable
data class GeocodedWayPoint(
    @SerialName("geocoder_status")
    val geocoderStatus: String? = "",
    @SerialName("place_id")
    val placeId: String? = "",
    val types: List<String>? = emptyList()
)
@Keep
@Serializable
data class RoutesResponseDto(
    @SerialName("overview_polyline")
    val overviewPolyline: PolylineDto? = null,
    val legs: List<LegsResponseDto>? = null,
    val bounds: BoundsDto? = null
)
@Keep
@Serializable
data class BoundsDto(
    val northeast: LocationDto? = null,
    val southwest: LocationDto? = null
)
@Keep
@Serializable
data class LegsResponseDto(
    val steps: List<StepDto>?= null,
    val distance: TextValueDto? = null,
    val duration: TextValueDto? = null
)
@Keep
@Serializable
data class TextValueDto(
    val text: String? = ""
)
@Keep
@Serializable
data class StepDto(
    val polyline: PolylineDto?= null,
    @SerialName("end_location")
    val endLocation: LocationDto? = null,
    @SerialName("start_location")
    val startLocation: LocationDto?= null
)
@Keep
@Serializable
data class LocationDto(
    val lat: Double? = null,
    val lng: Double? = null
)
@Keep
@Serializable
data class PolylineDto(
    val points: String? = null
)