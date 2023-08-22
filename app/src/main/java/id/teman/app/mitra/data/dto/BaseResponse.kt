package id.teman.app.mitra.data.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BaseResponse(
    val statusCode: Int? = null,
    val message: String? = null,
    val error: String? = null
)