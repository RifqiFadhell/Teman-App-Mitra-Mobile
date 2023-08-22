package id.teman.app.mitra.ui.notification

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.MainActivity
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.notification.domain.model.NotificationUiSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun NotificationScreen(
    navigator: DestinationsNavigator,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getNotifications()
    }
    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = uiState.exception, block = {
        uiState.exception?.consumeOnce {
            openDialog.value = it
        }
    })

    if (openDialog.value.isNotEmpty()) {
        GeneralDialogPrompt(title = "Ups, Ada Kesalahan", subtitle = openDialog.value,
            actionButtons = {
                GeneralActionButton(
                    text = "Ok",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = true
                ) {
                    openDialog.value = ""

                }
            }, dismissible = true
        ) {
            openDialog.value = ""
        }
    }
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            BasicTopNavigation(title = "Notifikasi") {
                navigator.popBackStack()
            }
            LazyColumn(
                contentPadding = PaddingValues(bottom = Theme.dimension.size_56dp)
            ) {
                items(uiState.successGetNotification) {
                    NotificationRowItem(item = it) {
                        try {
                            viewModel.readNotification(it.id)
                            (context as MainActivity).startActivity(
                                Intent(Intent.ACTION_VIEW, it.url.toUri())
                            )
                        } catch (e: Exception) {
                            e.stackTrace
                            Toast.makeText(
                                context,
                                "Mohon maaf, kami tidak dapat membuka url ini",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.primaryRed500
            )
        }
    }
}

@Composable
private fun NotificationRowItem(item: NotificationUiSpec, onNotificationClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_8dp)
            .border(
                BorderStroke(
                    width = Theme.dimension.size_1dp,
                    color = UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(
                horizontal = Theme.dimension.size_12dp,
                vertical = Theme.dimension.size_20dp
            )
            .clickable {
                onNotificationClick()
            }
    ) {
        TemanCircleButton(
            icon = item.type.icon,
            iconModifier = Modifier.size(Theme.dimension.size_24dp),
            circleModifier = Modifier
                .align(Alignment.CenterVertically)
                .size(Theme.dimension.size_40dp),
            circleBackgroundColor = item.type.backgroundColor,
            iconColor = item.type.iconColor
        )
        Column(
            modifier = Modifier
                .padding(start = Theme.dimension.size_8dp)
                .weight(1f)
        ) {
            Text(
                item.title,
                style = UiFont.poppinsCaptionSemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.subtitle,
                style = UiFont.poppinsCaptionMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            Text(
                item.date, style = UiFont.poppinsCaptionSmallMedium,
                modifier = Modifier.padding(start = Theme.dimension.size_4dp)
            )
            if (!item.isNotificationOpen) {
                Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(Theme.dimension.size_12dp)
                        .background(color = UiColor.blue)
                )
            }
        }
    }
}