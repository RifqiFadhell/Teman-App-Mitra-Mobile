package id.teman.app.mitra.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.TopBarWallet
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.dialog.GeneralActionButton
import id.teman.app.mitra.dialog.GeneralDialogPrompt
import id.teman.app.mitra.domain.model.wallet.WalletHistoryItemSpec
import id.teman.app.mitra.ui.destinations.TopUpWalletScreenDestination
import id.teman.app.mitra.ui.destinations.WebviewScreenDestination
import id.teman.app.mitra.ui.destinations.WithdrawalBankInformationScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.wallet.viewmodel.WalletViewModel
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
@Destination
fun WalletScreen(
    navigator: DestinationsNavigator,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.initPage()
    }

    val openDialog = remember { mutableStateOf("") }

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

    LaunchedEffect(key1 = uiState.error, block = {
        uiState.error?.consumeOnce {
            openDialog.value = it
        }
    })



    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = UiColor.neutral900)
            ) {
                TopBarWallet(
                    title = "Pendapatan Mitra", onWalletClick = {
                        navigator.navigate(TopUpWalletScreenDestination)
                    }, onBackClick = {
                        navigator.popBackStack()
                    })
                Spacer(modifier = Modifier.height(Theme.dimension.size_28dp))
                Text(
                    "Total Pendapatan",
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
                    uiState.balance.convertToRupiah(),
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
                            if (!uiState.isLoading) {
                                navigator.navigate(
                                    WithdrawalBankInformationScreenDestination(
                                        balance = uiState.balance
                                    )
                                )
                            }
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
                            icon = R.drawable.teman_wallet_new,
                            circleBackgroundColor = UiColor.success50,
                            circleModifier = Modifier.size(Theme.dimension.size_60dp),
                            iconModifier = Modifier.size(Theme.dimension.size_36dp)
                        )
                        Column(
                            modifier = Modifier.padding(start = Theme.dimension.size_16dp)
                        ) {
                            Text(
                                "Penarikan Dana",
                                style = UiFont.poppinsH5SemiBold
                            )
                            Text(
                                "Via Transfer Bank",
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
                            text = "Riwayat Pembayaran",
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
                                WalletTransactionItem(item = it) { url ->
                                    navigator.navigate(WebviewScreenDestination(url))
                                }
                            }
                        }
                    }
                }

            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun WalletTransactionItem(item: WalletHistoryItemSpec, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimension.size_16dp, vertical = Theme.dimension.size_8dp)
            .border(
                shape = RoundedCornerShape(Theme.dimension.size_12dp),
                color = UiColor.neutral50,
                width = Theme.dimension.size_1dp
            )
            .padding(Theme.dimension.size_16dp)
            .clickable {
                if (item.url.isNotNullOrEmpty()) {
                    onClick(item.url)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TemanCircleButton(
            icon = item.icon,
            circleModifier = Modifier.size(Theme.dimension.size_44dp),
            iconModifier = Modifier.size(Theme.dimension.size_28dp),
            circleBackgroundColor = UiColor.neutralGray0
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = UiFont.poppinsCaptionSemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                Text(
                    item.subtitle, style = UiFont.poppinsCaptionSemiBold.copy(
                        color = UiColor.neutral300
                    ), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.date, style = UiFont.poppinsCaptionMedium.copy(
                        color = UiColor.neutral300
                    ), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    item.price.convertToRupiah(), style = UiFont.poppinsCaptionSemiBold.copy(
                        color = UiColor.success500
                    )
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                Row {
                    GlideImage(
                        imageModel = R.drawable.ic_wallet_item_history,
                        modifier = Modifier.size(Theme.dimension.size_16dp)
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_4dp))
                    Text(
                        "Teman", style = UiFont.poppinsCaptionMedium.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            }
        }
    }
}