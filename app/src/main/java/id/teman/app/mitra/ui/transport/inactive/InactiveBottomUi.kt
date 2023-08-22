package id.teman.app.mitra.ui.transport.inactive

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.convertToKilometre
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.domain.model.transport.DriverOrderSummarySpec
import id.teman.app.mitra.domain.model.user.DriverMitraType
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.transport.TransportViewModel
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.InactiveBottomUi(
    modifier: Modifier = Modifier,
    isDriverActive: Boolean,
    inactiveUiSpec: TransportViewModel.InactiveUiSpec
) {
    Column(modifier = modifier.align(Alignment.BottomCenter)) {
        if (!isDriverActive) {
            DriverStatusUI(
                modifier = Modifier
                    .padding(horizontal = Theme.dimension.size_16dp),
                icon = {
                    GlideImage(
                        imageModel = R.drawable.ic_mitra_offline,
                        modifier = Modifier
                            .size(Theme.dimension.size_44dp)
                    )
                }
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
        }
        DriverTripDetails(inactiveUiSpec)
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun DriverStatusUI(
    modifier: Modifier = Modifier,
    icon: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = UiColor.white,
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_16dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column {
            Text(
                "Kamu Sedang Offline",
                style = UiFont.poppinsP3SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
            Text(
                "Aktifkan untuk mulai menerima orderan", style = UiFont.poppinsCaptionSemiBold.copy(
                    color = UiColor.neutral300,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
    }
}

@Composable
fun DriverTripDetails(inactiveUiSpec: TransportViewModel.InactiveUiSpec) {
    var expandableState by rememberSaveable { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (expandableState) 180f else 0f)

    val driverType = remember {
        if (inactiveUiSpec.userInfo.driverInfo?.mitraType == DriverMitraType.BIKE) "Mitra T-Bike"
        else "Mitra T-Car"
    }
    Card(
        elevation = Theme.dimension.size_6dp,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        backgroundColor = UiColor.white
    ) {
        Column(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
            DriverRowDetail(
                imageModel = R.drawable.ic_person,
                title = inactiveUiSpec.userInfo.name,
                subtitle = driverType,
                circleBackgroundColor = UiColor.primaryYellow500
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Theme.dimension.size_20dp),
                color = UiColor.neutral100,
                thickness = Theme.dimension.size_1dp
            )
            DriverRowDetail(
                imageModel = R.drawable.ic_dollar,
                title = inactiveUiSpec.driverOrderSummary.income.convertToRupiah(),
                subtitle = "Pendapatan hari ini",
                circleBackgroundColor = UiColor.success50,
                rotateState = rotateState,
                onButtonClick = {
                    expandableState = !expandableState
                }
            )
            AnimatedVisibility(
                visible = expandableState,
                enter = fadeIn() + expandVertically(
                    animationSpec = tween(durationMillis = 700, easing = LinearOutSlowInEasing)
                ),
                exit = fadeOut() + shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 700, easing = LinearOutSlowInEasing
                    )
                )
            ) {
                TodayTripDetail(inactiveUiSpec.driverOrderSummary)
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun DriverRowDetail(
    @DrawableRes imageModel: Int,
    title: String,
    subtitle: String,
    circleBackgroundColor: Color,
    rotateState: Float? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TemanCircleButton(
            icon = imageModel,
            circleBackgroundColor = circleBackgroundColor,
            circleModifier = Modifier.size(Theme.dimension.size_44dp),
            iconModifier = Modifier.size(Theme.dimension.size_24dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column {
            Text(
                title,
                style = UiFont.poppinsP3SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
            Text(
                subtitle, style = UiFont.poppinsCaptionSemiBold.copy(
                    color = UiColor.neutral300,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (onButtonClick != null && rotateState != null) {
            GlideImage(
                imageModel = R.drawable.ic_arrow_down,
                modifier = Modifier
                    .size(Theme.dimension.size_18dp)
                    .fillMaxSize()
                    .align(Alignment.CenterVertically)
                    .rotate(rotateState)
                    .clickable {
                        onButtonClick()
                    }
            )
        }
    }
}

@Composable
fun TodayTripDetail(driverOrderSummary: DriverOrderSummarySpec) {
    Column(
        modifier = Modifier
            .padding(top = Theme.dimension.size_20dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = Theme.dimension.size_1dp,
                    color = UiColor.neutral100
                ),
                shape = RoundedCornerShape(Theme.dimension.size_8dp)
            )
            .padding(Theme.dimension.size_16dp)
    ) {
        Text("Perjalanan Hari ini", style = UiFont.poppinsP3SemiBold)
        Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            TodayTripIconText(
                icon = R.drawable.ic_online_time,
                value = "${driverOrderSummary.onlineHours}",
                title = "JAM ONLINE"
            )
            TodayTripIconText(
                icon = R.drawable.ic_total_order_distance,
                value = driverOrderSummary.totalDistance.convertToKilometre(),
                title = "TOTAL JARAK"
            )
            TodayTripIconText(icon = R.drawable.ic_total_order, value = "${driverOrderSummary.totalJobs}", title = "TOTAL JOB")
        }
    }
}

@Composable
private fun TodayTripIconText(
    @DrawableRes icon: Int,
    value: String,
    title: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GlideImage(icon, modifier = Modifier.size(Theme.dimension.size_32dp))
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        Text(value, style = UiFont.poppinsH4sSemiBold)
        Text(title, style = UiFont.poppinsCaptionSmallSemiBold.copy(color = UiColor.neutral500))
    }
}
