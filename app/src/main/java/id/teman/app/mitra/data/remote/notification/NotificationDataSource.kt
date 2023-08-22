package id.teman.app.mitra.data.remote.notification

import id.teman.app.mitra.data.dto.notification.NotificationDto
import id.teman.app.mitra.data.dto.notification.NotificationReadRequestDto
import id.teman.app.mitra.data.dto.notification.NotificationReadResponseDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface NotificationDataSource {
    suspend fun getNotificationList(): Flow<NotificationDto>
    suspend fun updateNotificationRead(id: String): Flow<NotificationReadResponseDto>
}

class DefaultNotificationDataSource(
    private val httpClient: ApiServiceInterface
): NotificationDataSource {

    override suspend fun getNotificationList(): Flow<NotificationDto> =
        handleRequestOnFlow { httpClient.getNotifications() }

    override suspend fun updateNotificationRead(id: String): Flow<NotificationReadResponseDto> =
        handleRequestOnFlow { httpClient.readNotification(NotificationReadRequestDto(id)) }
}