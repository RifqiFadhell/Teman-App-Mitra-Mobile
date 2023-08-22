package id.teman.app.mitra.ui.transport.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import id.teman.app.mitra.R
import id.teman.app.mitra.common.convertToKilometre
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun OrderDetailItemHeader(order: TransportOrderSpec) {
    val distanceText =
        order.distanceText.ifEmpty { order.distance.convertToKilometre() }
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
                    order.customerName, style = UiFont.poppinsH5SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                Row {
                    Text(
                        "T-${order.driverType.type}",
                        style = UiFont.poppinsCaptionSmallMedium.copy(
                            color = UiColor.white,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        modifier = Modifier
                            .background(
                                color = UiColor.blue,
                                shape = RoundedCornerShape(Theme.dimension.size_8dp)
                            )
                            .padding(
                                vertical = Theme.dimension.size_2dp,
                                horizontal = Theme.dimension.size_8dp
                            )
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                    Text(
                        order.paymentMethod,
                        style = UiFont.poppinsCaptionSmallMedium.copy(
                            color = UiColor.tertiaryBlue500,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        modifier = Modifier
                            .background(
                                color = UiColor.tertiaryBlue50,
                                shape = RoundedCornerShape(Theme.dimension.size_8dp)
                            )
                            .padding(
                                vertical = Theme.dimension.size_2dp,
                                horizontal = Theme.dimension.size_8dp
                            )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    order.totalPrice.convertToRupiah(),
                    style = UiFont.poppinsSubHSemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Text(
                    distanceText,
                    style = UiFont.poppinsP2Medium.copy(
                        color = UiColor.neutral500,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }
        }
    }
}