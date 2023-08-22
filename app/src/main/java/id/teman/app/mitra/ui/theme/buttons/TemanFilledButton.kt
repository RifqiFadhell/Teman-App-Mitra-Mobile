package id.teman.app.mitra.ui.theme.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonState
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.IconArrangement
import id.teman.app.mitra.common.backgroundColorState
import id.teman.app.mitra.common.buttonContentTextStyle
import id.teman.app.mitra.common.buttonHeight
import id.teman.app.mitra.common.iconDimensForButton
import id.teman.app.mitra.common.iconLabelSpacing

@Composable
fun TemanFilledButton(
    modifier: Modifier = Modifier,
    content: String,
    isEnabled: Boolean = true,
    buttonType: ButtonType,
    activeColor: Color,
    borderRadius: Dp = 0.dp,
    activeTextColor: Color = Color.Black,
    @DrawableRes iconRes: Int? = null,
    iconArrangement: IconArrangement = IconArrangement.LeftArrangement,
    onClicked: () -> Unit
) {
    Button(
        modifier = modifier
            .height(buttonHeight(buttonType).dp),
        elevation = null,
        shape = RoundedCornerShape(borderRadius),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColorState(
                activeColor = activeColor,
                buttonState = ButtonState.Active
            ),
            disabledBackgroundColor = backgroundColorState(
                activeColor,
                buttonState = ButtonState.Disabled
            ),
            disabledContentColor = Color.Gray
        ),
        content = {
            val iconModifier = Modifier
                .width(iconDimensForButton(buttonType).dp)
                .height(iconDimensForButton(buttonType).dp)

            if (iconRes != null && iconArrangement == IconArrangement.LeftArrangement) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "",
                    modifier = iconModifier,
                    tint = if (isEnabled) Color.Transparent else Color(0xFFAEAEAE)
                )
                Spacer(modifier = Modifier.width(iconLabelSpacing(buttonType).dp))
            }
            Text(
                content,
                style = buttonContentTextStyle(
                    buttonType = buttonType,
                    isActive = isEnabled,
                    activeTextColor = activeTextColor
                ),
                textAlign = TextAlign.Center
            )
            if (iconRes != null && iconArrangement == IconArrangement.RightArrangement) {
                Spacer(modifier = Modifier.width(iconLabelSpacing(buttonType).dp))
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "",
                    modifier = iconModifier,
                    tint = if (isEnabled) activeTextColor else Color(0xFFAEAEAE)
                )
                Spacer(modifier = Modifier.width(iconLabelSpacing(buttonType).dp))
            }
        },
        onClick = {
            if (isEnabled) {
                onClicked()
            }
        }
    )
}

@Preview
@Composable
fun PreviewFilledButton() {
    Column {
        TemanFilledButton(
            content = "Button Large",
            buttonType = ButtonType.Large,
            activeColor = Color.Green,
            modifier = Modifier.padding(5.dp)
        ) {}

        TemanFilledButton(
            content = "Button Medium",
            buttonType = ButtonType.Medium,
            activeColor = Color.Green,
            modifier = Modifier.padding(5.dp)
        ) {}

        TemanFilledButton(
            content = "Button Small",
            buttonType = ButtonType.Small,
            activeColor = Color.Green,
            modifier = Modifier.padding(5.dp)
        ) {}

        TemanFilledButton(
            content = "Button Small Disabled",
            buttonType = ButtonType.Large,
            activeColor = Color.Green,
            isEnabled = false,
            modifier = Modifier.padding(5.dp)
        ) {}

        TemanFilledButton(
            content = "Button Small Disabled",
            buttonType = ButtonType.Small,
            activeColor = Color.Green,
            borderRadius = 30.dp,
            modifier = Modifier.padding(5.dp)
        ) {}

        TemanFilledButton(
            content = "Button Small Disabled",
            buttonType = ButtonType.Small,
            activeColor = Color.Green,
            borderRadius = 30.dp,
            iconRes = R.drawable.ic_launcher_background,
            modifier = Modifier.padding(5.dp)
        ) {}
        TemanFilledButton(
            content = "Button Small Disabled",
            buttonType = ButtonType.Large,
            activeColor = Color.Green,
            borderRadius = 30.dp,
            iconArrangement = IconArrangement.RightArrangement,
            iconRes = R.drawable.ic_launcher_background,
            modifier = Modifier.padding(5.dp)
        ) {}
        TemanFilledButton(
            content = "Button Small Disabled",
            buttonType = ButtonType.Large,
            activeColor = Color.Green,
            isEnabled = false,
            borderRadius = 30.dp,
            iconArrangement = IconArrangement.RightArrangement,
            iconRes = R.drawable.ic_launcher_background,
            modifier = Modifier.padding(5.dp)
        ) {}
    }
}