package id.teman.app.mitra.ui.myaccount.reward

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.TopBar
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.wallet.ItemRewardTransaction
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
@Destination
fun ListHistoryRewardScreen(
    navigator: DestinationsNavigator,
    viewModel: RewardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getHistoryPoint()
    }
    val openDialog = remember { mutableStateOf("") }
    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
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
            }, dismissible = true) {
            openDialog.value = ""
        }
    }
    Scaffold(
        topBar = {
            TopBar(title = "Riwayat Poin") {
                navigator.popBackStack()
            }
        }, content = {
            LazyColumn(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
                items(uiState.historyPoint) { item ->
                    TransactionPointHistory(item = item)
                }
            }
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun TransactionPointHistory(
    item: ItemRewardTransaction
) {
    Card(
        modifier = Modifier
            .padding(Theme.dimension.size_8dp)
            .clickable {

            },
        elevation = Theme.dimension.size_2dp,
        shape = RoundedCornerShape(Theme.dimension.size_12dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.dimension.size_16dp)
        ) {
            TemanCircleButton(
                icon = item.icon,
                circleBackgroundColor = UiColor.neutralGray0,
                circleModifier = Modifier
                    .size(Theme.dimension.size_48dp),
                iconModifier = Modifier
                    .size(Theme.dimension.size_24dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = Theme.dimension.size_8dp)
                    .weight(1f)
            ) {
                Text(
                    item.title,
                    style = UiFont.poppinsP2SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.description,
                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    item.amount.toString(),
                    style = UiFont.poppinsCaptionSemiBold.copy(
                        color = if (item.key == "driver_point" || item.key == "register_point") {
                            UiColor.success500
                        } else {
                            UiColor.primaryRed500
                        }
                    ),
                    modifier = Modifier.padding(start = Theme.dimension.size_4dp)
                )
            }
        }
    }
}

