package id.teman.app.mitra.data.dto.user.response

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SimpleUserDetailDto(
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("user_photo")
    val photo: UserPhotoDto? = null,
    val id: String? = null,
    val name : String? = null,
)

@Keep
@Serializable
data class UserPhotoDto(
    @SerialName("url")
    val url: String? = null
)