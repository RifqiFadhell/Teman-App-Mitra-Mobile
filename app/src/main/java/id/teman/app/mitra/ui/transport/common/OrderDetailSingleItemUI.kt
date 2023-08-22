package id.teman.app.mitra.ui.transport.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun OrderDetailSingleItemUI(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    showDivider: Boolean = true
) {
    Row(verticalAlignment = Alignment.Top) {
        GlideImage(
            imageModel = icon,
            modifier = Modifier.size(Theme.dimension.size_20dp),
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_14dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                title, style = UiFont.poppinsP2SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
            Text(
                subtitle, style = UiFont.poppinsP2Medium.copy(
                    color = UiColor.neutral500,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            if (showDivider) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.dimension.size_16dp),
                    color = UiColor.neutral100
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun OrderDetailSingleItemUIClickable(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(horizontal = Theme.dimension.size_4dp)) {
        GlideImage(
            imageModel = icon,
            modifier = Modifier.size(Theme.dimension.size_20dp),
            imageOptions = ImageOptions(
                colorFilter = ColorFilter.tint(color = UiColor.neutral900)
            )
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_14dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                title, style = UiFont.poppinsP2SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
            Text(
                subtitle, style = UiFont.poppinsP2Medium.copy(
                    color = UiColor.tertiaryBlue500,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ), modifier = Modifier.clickable {
                    onClick()
                }
            )
            if (showDivider) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.dimension.size_16dp),
                    color = UiColor.neutral100
                )
            }
        }
    }
}