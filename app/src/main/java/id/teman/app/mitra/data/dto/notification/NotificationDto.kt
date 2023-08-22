package id.teman.app.mitra.data.dto.notification

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotificationDto(
    val data: List<NotificationItemDto>? = emptyList(),
    val count: Int? = null,
    val page: Int? = null,
    val pageCount: Int? = null
)

@Keep
@Serializable
data class NotificationItemDto(
    val id: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val url: String? = null,
    val title: String? = null, 
    val description: String? = null, 
    val read: Boolean? = null,
    val type: String? = null
)