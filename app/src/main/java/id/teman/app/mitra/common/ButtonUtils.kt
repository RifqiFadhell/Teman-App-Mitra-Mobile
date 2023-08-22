package id.teman.app.mitra.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import id.teman.coreui.typography.UiFont

sealed class ButtonType {
    object Small : ButtonType()
    object Medium : ButtonType()
    object Large : ButtonType()
}

sealed class ButtonState {
    object Active : ButtonState()
    object Disabled : ButtonState()
}

fun backgroundColorState(activeColor: Color, buttonState: ButtonState): Color {
    // todo: add disabled color when color is ready
    return when (buttonState) {
        ButtonState.Active -> activeColor
        ButtonState.Disabled -> Color.Gray
    }
}

@Composable
fun buttonContentTextStyle(buttonType: ButtonType, isActive: Boolean, activeTextColor: Color): TextStyle {
    return when (buttonType) {
        ButtonType.Large -> UiFont.poppinsH4sSemiBold
        ButtonType.Medium -> UiFont.poppinsSubHSemiBold
        ButtonType.Small -> UiFont.poppinsCaptionSemiBold
    }.let {
        it.copy(
            color = if (isActive) activeTextColor else Color(0xFFAEAEAE)
        )
    }
}

fun iconDimensForButton(buttonType: ButtonType): Double = when (buttonType) {
    ButtonType.Large -> 13.5
    ButtonType.Medium -> 9.41
    ButtonType.Small -> 8.24
}

fun iconLabelSpacing(buttonType: ButtonType): Double = when (buttonType) {
    ButtonType.Large -> 7.41
    ButtonType.Medium -> 7.25
    ButtonType.Small -> 7.1
}

@Composable
private fun fixedSizeTextUnit(originalUnit: TextUnit): TextUnit =
    with(LocalDensity.current) {
        (originalUnit / fontScale)
    }

fun buttonHeight(buttonType: ButtonType): Int {
    return when (buttonType) {
        ButtonType.Large -> ButtonHeightDimens.HEIGHT_LARGE
        ButtonType.Medium -> ButtonHeightDimens.HEIGHT_MEDIUM
        ButtonType.Small -> ButtonHeightDimens.HEIGHT_SMALL
    }
}

object ButtonHeightDimens {
    const val HEIGHT_LARGE = 48
    const val HEIGHT_MEDIUM = 40
    const val HEIGHT_SMALL = 32
}