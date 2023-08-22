package id.teman.app.mitra.data.dto.user.request

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class RefreshTokenRequestDto(
    val token: String
)