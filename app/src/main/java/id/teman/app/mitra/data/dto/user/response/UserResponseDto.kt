package id.teman.app.mitra.data.dto.user.response

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserResponseDto(
    val role: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val name: String? = null,
    @SerialName("point")
    val point: Double? = null,
    @SerialName("id_card_number")
    val idCardNumber: String? = null,
    @SerialName("id_card_full_name")
    val idCardFullName: String? = null,
    val id: String? = null,
    val kyc: Boolean? = null,
    @SerialName("kyc_status")
    val kycStatus: String? = null,
    @SerialName("user_photo")
    val userPhotoDto: UserPhotoDto? = null,
    @SerialName("pin_status")
    val pinStatus: Boolean? = null,
    @SerialName("minimum_balance")
    val minimumBalance: Double? = null,
    @SerialName("referral_code")
    val referralCode: String? = null
)
