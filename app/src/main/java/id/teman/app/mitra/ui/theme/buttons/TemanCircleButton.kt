package id.teman.app.mitra.ui.theme.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.coreui.typography.UiColor

@Composable
fun TemanCircleButton(
    circleModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    iconColor: Color? = null,
    circleBackgroundColor: Color = UiColor.neutralGray0
) {
    Box(
        modifier = circleModifier
            .clip(CircleShape)
            .background(color = circleBackgroundColor)
    ) {
        GlideImage(
            imageModel = icon,
            modifier = iconModifier
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null ) ColorFilter.tint(color = iconColor) else null
            )
        )
    }
}

@Composable
fun TemanCircleButtonCLicked(
    circleModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    iconColor: Color? = null,
    circleBackgroundColor: Color = UiColor.neutralGray0,
    onClick: () -> Unit
) {
    Box(
        modifier = circleModifier
            .clip(CircleShape)
            .background(color = circleBackgroundColor)
            .clickable { onClick() }
    ) {
        GlideImage(
            imageModel = icon,
            modifier = iconModifier
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null ) ColorFilter.tint(color = iconColor) else null
            )
        )
    }
}