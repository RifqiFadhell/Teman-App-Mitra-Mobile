package id.teman.app.mitra.ui.transport.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import id.teman.app.mitra.R
import id.teman.app.mitra.domain.model.transport.TransportTopBarType
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.app.mitra.ui.transport.TransportViewModel
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable fun BoxScope.TransportHeaderWidget(
    uiState: TransportViewModel.TransportUiStateSpec,
    onChangeDriverStatus: (Boolean) -> Unit,
    onDrawerClick: () -> Unit
) {
    when (uiState.topBarType) {
        TransportTopBarType.DEFAULT_TOP_BAR -> {
            HeaderWidget(
                modifier = Modifier
                    .padding(
                        top = Theme.dimension.size_32dp,
                        end = Theme.dimension.size_16dp,
                        start = Theme.dimension.size_16dp
                    )
                    .align(Alignment.TopStart),
                isActiveStatus = uiState.isDriverActive,
                onChange = { isActive ->
                    if (uiState.exceptionCardUI == null) {
                        onChangeDriverStatus(isActive)
                    }
                },
                onDrawerClick = onDrawerClick
            )
        }
        TransportTopBarType.ONGOING_ORDER_TOP_BAR -> {
            uiState.driverAcceptedRequestUI?.let { spec ->
                val text = "Pesanan T-${spec.driverType.type}"
                Card(
                    modifier = Modifier
                        .padding(
                            top = Theme.dimension.size_32dp,
                            end = Theme.dimension.size_16dp,
                            start = Theme.dimension.size_16dp
                        )
                        .align(Alignment.TopStart),
                    elevation = Theme.dimension.size_12dp,
                    shape = RoundedCornerShape(Theme.dimension.size_32dp),
                    backgroundColor = UiColor.white
                ) {
                    Text(
                        text, style = UiFont.poppinsSubHSemiBold, modifier = Modifier.padding(
                            horizontal = Theme.dimension.size_20dp,
                            vertical = Theme.dimension.size_12dp
                        )
                    )
                }
            } ?: run {
                uiState.driverOnRouteRequestUI?.let { spec ->
                    val text = "Pesanan T-${spec.driverType.type}"
                    Card(
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_32dp,
                                end = Theme.dimension.size_16dp,
                                start = Theme.dimension.size_16dp
                            )
                            .align(Alignment.TopStart),
                        elevation = Theme.dimension.size_12dp,
                        shape = RoundedCornerShape(Theme.dimension.size_32dp),
                        backgroundColor = UiColor.white
                    ) {
                        Text(
                            text, style = UiFont.poppinsSubHSemiBold, modifier = Modifier.padding(
                                horizontal = Theme.dimension.size_20dp,
                                vertical = Theme.dimension.size_12dp
                            )
                        )
                    }
                }
            }

        }
        TransportTopBarType.FINISHED_TOP_BAR -> {
            TemanCircleButton(
                circleModifier = Modifier
                    .padding(
                        top = Theme.dimension.size_32dp,
                        end = Theme.dimension.size_16dp,
                        start = Theme.dimension.size_16dp
                    )
                    .align(Alignment.TopStart)
                    .size(Theme.dimension.size_48dp),
                iconModifier = Modifier.size(Theme.dimension.size_24dp),
                circleBackgroundColor = UiColor.white,
                icon = R.drawable.ic_arrow_left_long
            )
        }
        else -> Unit
    }
}

@Composable
private fun HeaderWidget(
    modifier: Modifier = Modifier,
    isActiveStatus: Boolean,
    onChange: (Boolean) -> Unit,
    onDrawerClick: () -> Unit
) {
    val switchColor = if (isActiveStatus) UiColor.blue else UiColor.white
    val textColor = if (isActiveStatus) UiColor.blue else UiColor.neutral900
    val textButton = if (isActiveStatus) "Aktif" else "Tidak Aktif"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Theme.dimension.size_48dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TemanCircleButton(
            icon = R.drawable.ic_hamburger,
            circleModifier = Modifier
                .size(Theme.dimension.size_48dp)
                .shadow(
                    elevation = Theme.dimension.size_12dp,
                    shape = RoundedCornerShape(Theme.dimension.size_32dp)
                )
                .clickable { onDrawerClick() },
            iconModifier = Modifier.size(Theme.dimension.size_24dp)
        )
        Card(
            elevation = Theme.dimension.size_12dp,
            shape = RoundedCornerShape(Theme.dimension.size_32dp),
            backgroundColor = UiColor.white
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_12dp
                    ),
            ) {
                Text(textButton, style = UiFont.poppinsSubHMedium.copy(color = textColor))
                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                Switch(
                    checked = isActiveStatus,
                    onCheckedChange = {
                        onChange(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = switchColor,
                        checkedTrackColor = UiColor.tertiaryBlue50,
                        uncheckedThumbColor = UiColor.neutral100
                    )
                )
            }
        }
    }
}