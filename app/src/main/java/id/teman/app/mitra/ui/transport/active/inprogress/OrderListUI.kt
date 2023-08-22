package id.teman.app.mitra.ui.transport.active.inprogress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonState
import id.teman.app.mitra.common.backgroundColorState
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.domain.model.user.DriverMitraType
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.transport.common.OrderDetailItemHeader
import id.teman.app.mitra.ui.transport.common.OrderDetailSingleItemUI
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.ActiveOrderList(
    order: TransportOrderSpec,
    onAcceptJob: (TransportOrderSpec) -> Unit,
    onSkipClick: (TransportOrderSpec) -> Unit
) {
    val configuration = LocalConfiguration.current
    val heightDp = configuration.screenHeightDp.dp
    Card(
        elevation = Theme.dimension.size_6dp,
        modifier = Modifier
            .heightIn(max = heightDp * 0.6f)
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp, topEnd = Theme.dimension.size_32dp
        ),
        backgroundColor = UiColor.white
    ) {
        val state = rememberScrollState()
        Column(modifier = Modifier
            .padding(Theme.dimension.size_16dp)
            .verticalScroll(state)) {
            OrderDetailItemHeader(order)
            Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
            OrderDetailSingleItemUI(
                icon = R.drawable.location, title = order.pickupAddress,
                subtitle = "Titik Penjemputan"
            )
            OrderDetailSingleItemUI(
                icon = R.drawable.location, title = order.destinationAddress,
                subtitle = "Titik Tujuan"
            )
            if (order.paymentMethod == "cash") {
                order.paymentBreakdown.forEachIndexed { index, payment ->
                    OrderDetailSingleItemUI(
                        icon = R.drawable.ic_dollar, title = payment.price.convertToRupiah(),
                        subtitle = payment.name,
                        showDivider = index < order.paymentBreakdown.size - 1
                    )
                }
            } else {
                if (order.driverType == DriverMitraType.FOOD) {
                    OrderDetailSingleItemUI(
                        icon = R.drawable.ic_dollar, title = "Pendapatan Kamu",
                        subtitle = order.driverIncome.convertToRupiah())
                    OrderDetailSingleItemUI(
                        icon = R.drawable.ic_dollar, title = "DiBayarkan Ke Restoran",
                        subtitle = order.totalPriceResto.convertToRupiah())
                } else {
                    OrderDetailSingleItemUI(
                        icon = R.drawable.ic_dollar, title = "Pendapatan Kamu",
                        subtitle = order.driverIncome.convertToRupiah())
                }
            }
            BottomButtonUI(order, onAcceptJob = onAcceptJob, onSkipClick = {
                onSkipClick(order)
            })
        }
    }
}

@Composable
private fun BottomButtonUI(
    item: TransportOrderSpec,
    onAcceptJob: (TransportOrderSpec) -> Unit,
    onSkipClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimension.size_16dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(
                Theme.dimension.size_30dp
            ),
            border = BorderStroke(
                color = UiColor.primaryRed500,
                width = Theme.dimension.size_1dp
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = Theme.dimension.size_0dp
            ),
            content = {
                Text(
                    "Lewati",
                    style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            onClick = onSkipClick
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
        Button(
            modifier = Modifier.weight(1f),
            elevation = null,
            shape = RoundedCornerShape(Theme.dimension.size_30dp),
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColorState(
                    activeColor = UiColor.primaryRed500,
                    buttonState = ButtonState.Active
                ),
                disabledBackgroundColor = backgroundColorState(
                    UiColor.primaryRed500,
                    buttonState = ButtonState.Disabled
                ),
                disabledContentColor = Color.Gray
            ),
            content = {
                Text(
                    "Terima Job",
                    style = UiFont.poppinsP1SemiBold.copy(color = UiColor.white),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            onClick = {
                onAcceptJob(item)
            }
        )

    }
}