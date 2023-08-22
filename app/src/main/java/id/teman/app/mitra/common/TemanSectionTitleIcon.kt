package id.teman.app.mitra.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiFont

@Composable
fun TemanSectionTitleIcon(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Start) {
        GlideImage(
            imageModel = icon,
            modifier = Modifier.size(Theme.dimension.size_16dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
        Text(title, style = UiFont.poppinsCaptionSemiBold)
    }
}