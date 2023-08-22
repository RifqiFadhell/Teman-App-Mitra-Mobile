package id.teman.app.mitra.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
fun CustomChip(
    title: String,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    backgroundColor: Color = UiColor.tertiaryBlue50,
    borderColor: Color = UiColor.neutral50,
    contentColor: Color = UiColor.neutral900,
    textColor: Color = UiColor.white,
    onClick: () -> Unit
) {
    Chip(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(
            ChipDefaults.OutlinedBorderSize,
            borderColor
        ),
        colors = ChipDefaults.chipColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        content = {
            Text(
                title,
                style = UiFont.poppinsP2SemiBold
                    .copy(
                        color = textColor, platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                modifier = textModifier,
                textAlign = TextAlign.Center
            )
        }
    )
}