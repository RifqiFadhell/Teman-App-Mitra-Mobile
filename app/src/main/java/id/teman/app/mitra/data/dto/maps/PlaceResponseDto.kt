package id.teman.app.mitra.data.dto.maps

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PlaceResponseDto(
    val result: PlacesResultDto? = null
)

@Keep
@Serializable
data class PlacesResultDto(
    val geometry: GeometryDto? = null,
    @SerialName("formatted_address")
    val formattedAddress: String? = null
)

@Keep
@Serializable
data class GeometryDto(
    val location: LocationDto? = null,
    val viewport: ViewPortDto? = null
)

@Keep
@Serializable
data class ViewPortDto(
    val northeast: LocationDto? = null,
    val southwest: LocationDto? = null
)