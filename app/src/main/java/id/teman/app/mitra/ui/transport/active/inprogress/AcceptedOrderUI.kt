package id.teman.app.mitra.ui.transport.active.inprogress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonState
import id.teman.app.mitra.common.backgroundColorState
import id.teman.app.mitra.common.convertToKilometre
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.minutesToReadableText
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.domain.model.transport.TransportRequestType
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.AcceptedOrderUI(
    item: TransportOrderSpec,
    onDetailClick: (TransportOrderSpec) -> Unit,
    onDeliverClick: (TransportOrderSpec) -> Unit,
    isDriverOnPoint: Boolean = false
) {
    val isEnrouteToCustomer = item.orderStatus == TransportRequestType.ACCEPTED
    var isVisible by remember { mutableStateOf(true) }
    Card(
        elevation = Theme.dimension.size_6dp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp, topEnd = Theme.dimension.size_32dp
        ),
        backgroundColor = UiColor.white
    ) {
        AnimatedVisibility(
            visible = isVisible,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.dimension.size_16dp)
                ) {
                    Divider(
                        modifier = Modifier
                            .padding(vertical = Theme.dimension.size_10dp)
                            .align(Alignment.CenterHorizontally)
                            .width(Theme.dimension.size_100dp)
                            .height(Theme.dimension.size_4dp)
                            .draggable(
                                orientation = Orientation.Horizontal,
                                state = rememberDraggableState { delta ->

                                }
                            )
                            .noRippleClickable {
                                isVisible = !isVisible
                            },
                        color = UiColor.neutral500,
                        thickness = Theme.dimension.size_4dp
                    )
                    AcceptedHeaderOrderUI(item)
                    AcceptedMiniDetailOrderUI(item)
                    AcceptedBottomButtonsUI(isEnrouteToCustomer, typeOrder = item.driverType.type, onDetailClick = {
                        onDetailClick(item)
                    }, onDeliverClick = {
                        onDeliverClick(item)
                    }, isDriverOnPoint = isDriverOnPoint)
                    Text(
                        "Tekan tombol ini bila Kamu sudah sampai di titik penjemputan",
                        style = UiFont.poppinsCaptionSmallMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
        AnimatedVisibility(visible = !isVisible) {
            Text(
                "Buka Order detail",
                style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(
                            topEnd = Theme.dimension.size_16dp,
                            topStart = Theme.dimension.size_16dp
                        ),
                        color = UiColor.neutral100
                    )
                    .padding(Theme.dimension.size_16dp)
                    .clickable {
                        isVisible = !isVisible
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun AcceptedHeaderOrderUI(item: TransportOrderSpec) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TemanCircleButton(
            icon = R.drawable.ic_person,
            circleBackgroundColor = UiColor.primaryYellow500,
            circleModifier = Modifier.size(Theme.dimension.size_44dp),
            iconModifier = Modifier.size(Theme.dimension.size_24dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    item.destinationAddress, style = UiFont.poppinsH5SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                Text(
                    "Titik Penjemputan", style = UiFont.poppinsCaptionSemiBold.copy(
                        color = UiColor.neutral300,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun AcceptedMiniDetailOrderUI(item: TransportOrderSpec) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimension.size_20dp)
    ) {
        TitleSubtitleUI(title = minutesToReadableText(item.duration), subtitle = "Estimasi")
        TitleSubtitleUI(title = item.distance.convertToKilometre(), subtitle = "Jarak")
        TitleSubtitleUI(title = item.totalPrice.convertToRupiah(), subtitle = "Total Harga")
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun TitleSubtitleUI(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title, style = UiFont.poppinsP2SemiBold.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
        Text(
            subtitle, style = UiFont.poppinsCaptionSemiBold.copy(
                color = UiColor.neutral500,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}

@Composable
private fun AcceptedBottomButtonsUI(
    isEnrouteToCustomer: Boolean,
    typeOrder: String? = null,
    onDetailClick: () -> Unit,
    onDeliverClick: () -> Unit,
    isDriverOnPoint: Boolean = false
) {
    val buttonText = if (isEnrouteToCustomer) {
        when(typeOrder) {
            "food" -> "Saya Sudah Sampai Restoran"
            else -> "Saya Sudah Sampai"
        }
    } else {
        when(typeOrder) {
            "food" -> "Makanan Telah di Drop-Off"
            "send" -> "Barang Telah di Drop-Off"
            else -> "Pelanggan Telah di Drop-Off"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimension.size_16dp),
    ) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
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
                    "Detail Pesanan",
                    style = UiFont.poppinsP1SemiBold.copy(color = UiColor.primaryRed500),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            onClick = onDetailClick
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            elevation = null,
            shape = RoundedCornerShape(Theme.dimension.size_30dp),
            enabled = isDriverOnPoint,
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
                    buttonText,
                    style = UiFont.poppinsP1SemiBold.copy(color = UiColor.white),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            onClick = onDeliverClick
        )

    }
}