package id.teman.app.mitra.ui.myaccount.reward

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.EmptyState
import id.teman.app.mitra.common.TopBar
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.wallet.ItemReward
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
@Destination
fun ListRewardScreen(
    navigator: DestinationsNavigator,
    viewModel: RewardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.initPageListReward()
    }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Sukses Menukar Hadiah") }
    var description by remember { mutableStateOf("Silahkan Kembali ke Halaman Reward") }
    LaunchedEffect(key1 = uiState.successRedeem, block = {
        uiState.successRedeem?.consumeOnce {
            showDialog = true
            title = "Silahkan Kembali ke Halaman Reward"
            description = "Silahkan Kembali ke Halaman Reward"
        }
    })

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
            TopBar(title = "List Reward") {
                navigator.popBackStack()
            }
        }, content = {
            LazyColumn(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
                items(uiState.rewards.size) { index ->
                    RewardItem(item = uiState.rewards[index], onClick = { id, point ->
                        if (uiState.rewardPoint.toInt() > point.orZero()) {
                            viewModel.redeemReward(id)
                        } else {
                            showDialog = true
                            title = "Point-mu belum cukup..."
                            description = "Silahkan Kembali ke Halaman Reward"
                        }
                    })
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
            if (uiState.rewards.isEmpty()) EmptyState(
                icon = R.drawable.ic_no_promo,
                title = "Belum Ada Rewards",
                description = "Tenang aja, kita bakal kabari kamu ketika ada Reward baru."
            )
            if (showDialog) {
                GeneralDialogPrompt(
                    title = title,
                    subtitle = description,
                    actionButtons = {
                        GeneralActionButton(
                            text = "Oke",
                            textColor = UiColor.primaryRed500,
                            isFirstAction = false,
                            onClick = {
                                showDialog = false
                                navigator.popBackStack()
                            }
                        )
                    },
                    onDismissRequest = {
                        showDialog = false
                        navigator.popBackStack()
                    }
                )
            }
        })
}

@Composable
fun RewardItem(item: ItemReward, onClick: (String, Int) -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = Theme.dimension.size_16dp)
            .border(
                BorderStroke(
                    Theme.dimension.size_1dp,
                    color = UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_4dp)
    ) {
        GlideImage(
            modifier = Modifier
                .aspectRatio(3f / 1f)
                .clip(RoundedCornerShape(Theme.dimension.size_12dp)),
            imageModel = item.url,
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
        Text(
            item.title,
            modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
            style = UiFont.poppinsSubHSemiBold
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        Row(
            modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                imageModel = R.drawable.ic_calendar,
                modifier = Modifier.size(Theme.dimension.size_16dp)
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
            Text(
                item.endDate,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral300)
            )
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Theme.dimension.size_16dp,
                        end = Theme.dimension.size_4dp,
                    ),
                content = "Tukar",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_8dp,
                activeTextColor = UiColor.white
            ) {
                onClick(item.id, item.point)
            }
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
    }
}