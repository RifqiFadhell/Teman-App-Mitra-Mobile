package id.teman.app.mitra.data.dto.user.request

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DriverStatusUpdateRequestDto(
    val status: String
)