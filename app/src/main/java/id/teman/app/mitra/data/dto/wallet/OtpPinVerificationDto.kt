package id.teman.app.mitra.data.dto.wallet

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OtpPinVerificationDto(
    val token: String? = null,
    val message: String? = null
)