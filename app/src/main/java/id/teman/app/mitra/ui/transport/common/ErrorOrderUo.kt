package id.teman.app.mitra.ui.transport.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonState
import id.teman.app.mitra.common.backgroundColorState
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.ErrorOrderUI(message: String = "Silahkan coba lagi dengan menekan tombol muat ulang", onRetry: () -> Unit) {
    Card(
        elevation = Theme.dimension.size_6dp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp, topEnd = Theme.dimension.size_32dp
        ),
        backgroundColor = UiColor.white
    ) {
        Column(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
            Text(
                "Oopps, ada kesalahan", style = UiFont.poppinsH5Bold, textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        top = Theme.dimension.size_32dp, bottom = Theme.dimension.size_16dp,
                        start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp
                    )
            )
            GlideImage(
                imageModel = R.drawable.ic_empty_order,
                modifier = Modifier.aspectRatio(2 / 1f),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Fit
                )
            )
            Text(
                message,
                style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500),
                modifier = Modifier.padding(
                    top = Theme.dimension.size_20dp, bottom = Theme.dimension.size_32dp,
                    start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp
                )
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                elevation = null,
                shape = RoundedCornerShape(Theme.dimension.size_30dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColorState(
                        activeColor = UiColor.primaryRed500,
                        buttonState = ButtonState.Active
                    ),
                    disabledBackgroundColor = backgroundColorState(
                        UiColor.primaryRed500,
                        buttonState = ButtonState.Disabled
                    ),
                    disabledContentColor = Color.Gray
                ),
                content = {
                    Text(
                        "Muat Ulang",
                        style = UiFont.poppinsP1SemiBold.copy(color = UiColor.white),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = onRetry
            )
        }
    }
}