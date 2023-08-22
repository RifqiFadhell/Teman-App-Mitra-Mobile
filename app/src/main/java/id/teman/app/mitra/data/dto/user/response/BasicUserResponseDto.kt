package id.teman.app.mitra.data.dto.user.response

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BasicUserResponseDto(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: UserResponseDto? = null,
    val driver: DriverBasicInfoDto? = null,
    val restaurant: MitraRestaurantBasicInfoDto? = null
)

