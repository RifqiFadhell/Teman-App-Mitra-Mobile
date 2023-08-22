package id.teman.app.mitra.ui.notification.domain.model

import id.teman.app.mitra.R as RApp
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import id.teman.app.mitra.common.convertToNotificationDate
import id.teman.app.mitra.common.orFalse
import id.teman.app.mitra.data.dto.notification.NotificationDto
import id.teman.coreui.typography.UiColor

enum class NotificationType(
    @DrawableRes val icon: Int,
    val backgroundColor: Color,
    val iconColor: Color
) {
    Order(RApp.drawable.ic_teman_food, UiColor.primaryRed50, UiColor.primaryRed500),
    Rating(RApp.drawable.ic_rating, UiColor.success50, UiColor.success500),
    Update(RApp.drawable.ic_rating, UiColor.success50, UiColor.success500)
}

data class NotificationUiSpec(
    val type: NotificationType,
    val title: String,
    val subtitle: String,
    val date: String,
    val isNotificationOpen: Boolean,
    val url: String,
    val id: String
)

fun NotificationDto.toListNotificationSpec(): List<NotificationUiSpec> {
    if (data.isNullOrEmpty() && pageCount == 0) return emptyList()
    return data?.map {
        NotificationUiSpec(
            id = it.id.orEmpty(),
            title = it.title.orEmpty(),
            subtitle = it.description.orEmpty(),
            date = it.createdAt.orEmpty().convertToNotificationDate(),
            isNotificationOpen = it.read.orFalse(),
            url = it.url.orEmpty(),
            type = if (it.type == "update" || it.type == "rating") NotificationType.Rating else NotificationType.Rating
        )
    }.orEmpty()
}