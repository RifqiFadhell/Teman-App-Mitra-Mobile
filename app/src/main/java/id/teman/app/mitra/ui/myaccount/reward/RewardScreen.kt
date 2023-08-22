package id.teman.app.mitra.ui.myaccount.reward

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.RewardTopBar
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.wallet.ItemRewardRedeemed
import id.teman.app.mitra.ui.destinations.ListHistoryRewardScreenDestination
import id.teman.app.mitra.ui.destinations.ListRewardScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
@Destination
fun RewardScreen(
    navigator: DestinationsNavigator,
    viewModel: RewardViewModel = hiltViewModel()
) {
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getUserProfile()
        viewModel.initPageReward()
    }
    val uiState = viewModel.uiState
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = UiColor.neutral900)
            ) {
                TopBarReward(
                    title = "Point Reward Mitra", onBackClick = {
                        navigator.popBackStack()
                    }) {
                    navigator.navigate(ListHistoryRewardScreenDestination)
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_28dp))
                Text(
                    "Total Point",
                    style = UiFont.poppinsSubHSemiBold.copy(
                        color = UiColor.white,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    modifier = Modifier.padding(horizontal = Theme.dimension.size_40dp)
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                Text(
                    uiState.rewardPoint,
                    style = UiFont.poppinsH1Bold.copy(
                        color = UiColor.white,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    modifier = Modifier.padding(horizontal = Theme.dimension.size_40dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimension.size_16dp
                        )
                        .offset(
                            x = Theme.dimension.size_0dp,
                            y = Theme.dimension.size_32dp
                        )
                        .clickable {
                            navigator.navigate(ListRewardScreenDestination)
                        },
                    elevation = Theme.dimension.size_2dp,
                    shape = RoundedCornerShape(Theme.dimension.size_12dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Theme.dimension.size_16dp,
                                vertical = Theme.dimension.size_24dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TemanCircleButton(
                            icon = R.drawable.ic_wallet_withdrawal,
                            circleBackgroundColor = UiColor.success50,
                            circleModifier = Modifier.size(Theme.dimension.size_60dp),
                            iconModifier = Modifier.size(Theme.dimension.size_36dp)
                        )
                        Column(
                            modifier = Modifier.padding(start = Theme.dimension.size_16dp)
                        ) {
                            Text(
                                "Daftar Reward",
                                style = UiFont.poppinsH5SemiBold
                            )
                            Text(
                                "Lihat Daftar Reward",
                                style = UiFont.poppinsCaptionMedium.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                    color = UiColor.neutral500
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        GlideImage(
                            modifier = Modifier.size(Theme.dimension.size_36dp),
                            imageModel = R.drawable.ic_arrow_right,
                            imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.black))
                        )
                    }
                }
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Riwayat Penukaran",
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_64dp,
                                    start = Theme.dimension.size_16dp,
                                    end = Theme.dimension.size_16dp
                                )
                                .align(Alignment.Start),
                            style = UiFont.poppinsP3SemiBold,
                            color = UiColor.neutral900
                        )
                        LazyColumn {
                            items(uiState.transactions) {
                                RewardItemRedeemed(item = it)
                            }
                        }
                    }
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

@Composable
fun RewardItemRedeemed(item: ItemRewardRedeemed) {
    Column(
        modifier = Modifier
            .padding(horizontal = Theme.dimension.size_16dp, vertical =Theme.dimension.size_8dp)
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
            imageModel = item.reward.url,
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
        Text(
            item.reward.title,
            modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
            style = UiFont.poppinsSubHSemiBold
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        Row(modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp), verticalAlignment = Alignment.CenterVertically) {
            GlideImage(
                imageModel = R.drawable.ic_calendar,
                modifier = Modifier.size(Theme.dimension.size_16dp)
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
            Text(
                item.date,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral300)
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_24dp))
            Text(
                item.status,
                modifier = Modifier.fillMaxWidth(),
                style = UiFont.poppinsSubHSemiBold,
                color = if (item.status == "success") UiColor.success500 else UiColor.neutral300,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
    }
}

@Composable
fun TopBarReward(title: String, onBackClick: () -> Unit, onHistoryClick: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.Transparent,
        contentPadding = PaddingValues(
            horizontal = Theme.dimension.size_16dp,
            vertical = Theme.dimension.size_8dp
        ),
        modifier = Modifier.height(Theme.dimension.size_56dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                GlideImage(
                    imageModel = R.drawable.ic_arrow_back,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(Theme.dimension.size_24dp)
                        .clickable {
                            onBackClick()
                        },
                    imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.white))
                )
                Text(
                    title,
                    textAlign = TextAlign.Start,
                    style = UiFont.poppinsH5SemiBold,
                    modifier = Modifier.padding(start = Theme.dimension.size_12dp),
                    color = UiColor.white
                )
            }
            Row(
                modifier = Modifier
                    .background(
                        Color.Transparent
                    )
                    .border(
                        border = BorderStroke(
                            width = Theme.dimension.size_1dp,
                            color = UiColor.neutral700
                        ), shape = RoundedCornerShape(Theme.dimension.size_24dp)
                    )
                    .padding(
                        vertical = Theme.dimension.size_8dp,
                        horizontal = Theme.dimension.size_12dp
                    )
                    .clickable {
                        onHistoryClick()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                GlideImage(
                    R.drawable.ic_nav_bottom_order,
                    modifier = Modifier.size(Theme.dimension.size_16dp)
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                Text(
                    "Riwayat Poin",
                    style = UiFont.poppinsCaptionSemiBold.copy(color = UiColor.success500)
                )
            }
        }
    }
}