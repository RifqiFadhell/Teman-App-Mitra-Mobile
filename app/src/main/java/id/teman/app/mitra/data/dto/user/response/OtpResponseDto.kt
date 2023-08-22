package id.teman.app.mitra.data.dto.user.response

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OtpResponseDto(
    val status: Int? = null,
    val message: String? = null,
    @SerialName("attemption")
    val attempt: Int? = null,
    @SerialName("wait_in_seconds")
    val waitTimer: Int? = null
)