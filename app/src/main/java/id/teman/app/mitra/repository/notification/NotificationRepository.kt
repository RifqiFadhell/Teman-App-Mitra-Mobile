package id.teman.app.mitra.repository.notification

import id.teman.app.mitra.data.remote.notification.NotificationDataSource
import id.teman.app.mitra.ui.notification.domain.model.NotificationUiSpec
import id.teman.app.mitra.ui.notification.domain.model.toListNotificationSpec
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepository @Inject constructor(
    private val remoteData: NotificationDataSource
){
    suspend fun getNotifications(): Flow<List<NotificationUiSpec>> =
        remoteData.getNotificationList()
            .map { it.toListNotificationSpec() }

    suspend fun readNotification(id: String): Flow<String> =
        remoteData.updateNotificationRead(id).map { it.message }
}