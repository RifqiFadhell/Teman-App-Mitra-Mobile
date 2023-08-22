package id.teman.app.mitra.data.dto.transport

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateRequestStatusDto(
    val status: String
)