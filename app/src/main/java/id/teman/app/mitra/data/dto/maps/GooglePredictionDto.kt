package id.teman.app.mitra.data.dto.maps

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GooglePredictionsDto(
    val predictions: List<GooglePredictionDto>? = null
)

@Keep
@Serializable
data class GooglePredictionDto(
    val description: String? = null,
    @SerialName("structured_formatting")
    val structuredFormatting: StructuredFormattingAddressDto? = null,
    @SerialName("place_id")
    val placeId: String? = null
)

@Keep
@Serializable
data class StructuredFormattingAddressDto(
    @SerialName("main_text")
    val title: String? = null,
    @SerialName("secondary_text")
    val description: String? = null
)