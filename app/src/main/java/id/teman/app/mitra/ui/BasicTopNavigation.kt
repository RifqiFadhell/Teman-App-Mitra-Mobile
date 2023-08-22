package id.teman.app.mitra.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun BasicTopNavigation(
    modifier: Modifier = Modifier,
    title: String,
    textColor: Color = UiColor.neutral900,
    iconColor: Color? = null,
    onBackButtonClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_16dp),
        horizontalArrangement = Arrangement.Start
    ) {
        GlideImage(
            imageModel = R.drawable.ic_arrow_back,
            modifier = Modifier
                .size(Theme.dimension.size_24dp)
                .clickable {
                    onBackButtonClick()
                },
            imageOptions = ImageOptions(
                colorFilter = if (iconColor != null) ColorFilter.tint(iconColor) else null
            )
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Text(title, style = UiFont.poppinsH5SemiBold.copy(
            color = textColor,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        ))
    }
}