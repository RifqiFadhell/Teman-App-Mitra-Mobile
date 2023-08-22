package id.teman.app.mitra.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    title: String? = "",
    description: String,
    isShowButton: Boolean = false,
    onCLick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlideImage(
            imageModel = icon,
            modifier = Modifier
                .fillMaxWidth()
                .size(237.dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
        )
        if (title.isNotNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(
                    top = Theme.dimension.size_40dp,
                    start = Theme.dimension.size_36dp,
                    end = Theme.dimension.size_36dp
                ), text = title.orEmpty(), style = UiFont.poppinsH3Bold
            )
        }
        Text(
            modifier = Modifier.padding(
                horizontal = Theme.dimension.size_36dp,
                vertical = Theme.dimension.size_16dp
            ), text = description, style = UiFont.poppinsP3Medium
        )
        if (isShowButton) TemanFilledButton(
            content = "Bagikan Sekarang",
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = true,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.dimension.size_28dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                ),
            onClicked = {
                onCLick?.invoke()
            }
        )
    }
}