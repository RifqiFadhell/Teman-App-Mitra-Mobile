package id.teman.app.mitra.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun CenteredTopNavigation(
    modifier: Modifier = Modifier,
    title: String,
    onBackButtonClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_16dp)
    ) {
        GlideImage(
            imageModel = R.drawable.ic_arrow_back,
            modifier = Modifier
                .size(Theme.dimension.size_24dp)
                .clickable {
                    onBackButtonClick()
                }
        )
        Text(
            title, style = UiFont.poppinsH5SemiBold.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ), modifier = Modifier.align(Alignment.Center)
        )
    }
}