package id.teman.app.mitra.data.dto.chat
import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ChatResponseDto(
    val data: List<ChatDetailResponseDto>? = null
)

@Keep
@Serializable
data class ChatDetailResponseDto(
    val id: String? = null,
    @SerialName("created_at")
    val sentTime: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    val text: String? = null
)