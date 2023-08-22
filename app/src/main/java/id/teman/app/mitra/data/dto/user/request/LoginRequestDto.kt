package id.teman.app.mitra.data.dto.user.request

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LoginRequestDto(
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("fcm_token")
    val fcmToken: String,
    val referral_code: String? = ""
)