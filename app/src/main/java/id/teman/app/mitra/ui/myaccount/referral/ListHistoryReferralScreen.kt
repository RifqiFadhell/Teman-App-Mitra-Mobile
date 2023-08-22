package id.teman.app.mitra.ui.myaccount.referral

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.R
import id.teman.app.mitra.common.DisposableEffectOnLifecycleEvent
import id.teman.app.mitra.common.EmptyState
import id.teman.app.mitra.common.TopBar
import id.teman.app.mitra.domain.model.referral.ItemReferral
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Destination
fun ListHistoryReferralScreen(
    navigator: DestinationsNavigator,
    viewModel: ReferralViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getHistoryReferral()
    }
    Scaffold(
        topBar = {
            TopBar(title = "Riwayat Referral") {
                navigator.popBackStack()
            }
        }, content = {
            if (uiState.listReferral.isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
                    items(uiState.listReferral) { item ->
                        ReferralHistory(item = item)
                    }
                }
            } else {
                EmptyState(
                    icon = R.drawable.ic_empty_order,
                    title = "Referral kamu belum ada yang gunakan",
                    description = "Yuk Bagikan sekarang kode referral kamu, dapatkan ribuan point saat Teman-mu berhasil mendaftar.",
                    isShowButton = true
                ) {
                    navigator.popBackStack()
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
fun ReferralHistory(
    item: ItemReferral
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
                icon = R.drawable.ic_person_mamoji,
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
                    item.name,
                    style = UiFont.poppinsP2SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Bergabung pada ${item.dateJoined}",
                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${item.benefit} Point",
                    style = UiFont.poppinsCaptionSemiBold.copy(
                        color = UiColor.success500
                    ),
                    modifier = Modifier.padding(start = Theme.dimension.size_4dp)
                )
            }
        }
    }
}