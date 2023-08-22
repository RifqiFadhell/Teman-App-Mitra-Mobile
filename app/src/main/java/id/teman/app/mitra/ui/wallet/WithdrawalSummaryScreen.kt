package id.teman.app.mitra.ui.wallet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.CenteredTopNavigation
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.domain.model.wallet.WalletDataTransferSpec
import id.teman.app.mitra.ui.destinations.WithdrawPinConfirmationScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun WithdrawalSummaryScreen(navigator: DestinationsNavigator, spec: WalletDataTransferSpec) {
    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    }
    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Penarikan Dana Mitra") {
                navigator.popBackStack()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Theme.dimension.size_16dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = UiColor.neutralGray0,
                            shape = RoundedCornerShape(Theme.dimension.size_16dp)
                        )
                        .padding(
                            Theme.dimension.size_16dp
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bank", style = UiFont.poppinsP2Medium)
                        Text(spec.bankName, style = UiFont.poppinsP2SemiBold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nomor Rekening", style = UiFont.poppinsP2Medium)
                        Text(spec.accountNumber, style = UiFont.poppinsP2SemiBold)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pemilik Rekening", style = UiFont.poppinsP2Medium)
                        Text(spec.accountName, style = UiFont.poppinsP2SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_40dp))
                Text("Rincian Pesanan", style = UiFont.poppinsP2SemiBold)
                Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tarik Pendapatan Mitra", style = UiFont.poppinsP2Medium)
                    Text(spec.withdrawalAmount.convertToRupiah(), style = UiFont.poppinsP2Medium)
                }
                Canvas(
                    Modifier
                        .padding(vertical = Theme.dimension.size_20dp)
                        .fillMaxWidth()
                        .height(Theme.dimension.size_2dp)
                ) {
                    drawLine(
                        color = UiColor.neutral200,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total yang dibayarkan", style = UiFont.poppinsP2SemiBold)
                    Text(spec.withdrawalAmount.convertToRupiah(), style = UiFont.poppinsP2SemiBold)
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                content = "Konfirmasi Penarikan",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white
            ) {
                navigator.navigate(WithdrawPinConfirmationScreenDestination(spec))
            }
        }
    )
}