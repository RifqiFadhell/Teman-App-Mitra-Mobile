package id.teman.app.mitra.ui.transport.active.completed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.OrderCompleted(fare: Double, onNavigateHome: () -> Unit) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(
                    topEnd = Theme.dimension.size_32dp,
                    topStart = Theme.dimension.size_32dp,
                ),
                color = Color.White
            )
            .padding(vertical = Theme.dimension.size_32dp, horizontal = Theme.dimension.size_16dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Perjalanan Selesai",
            textAlign = TextAlign.Center,
            style = UiFont.poppinsH5Bold
        )
        GlideImage(
            imageModel = R.drawable.ic_done,
            modifier = Modifier.size(Theme.dimension.size_200dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit
            )
        )
        Text(
            "Terimakasih telah percaya kepada kami.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                top = Theme.dimension.size_20dp,
                bottom = Theme.dimension.size_36dp
            ),
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = Theme.dimension.size_32dp)
                .fillMaxWidth()
                .border(
                    border = BorderStroke(Theme.dimension.size_1dp, color = UiColor.neutral100),
                    shape = RoundedCornerShape(Theme.dimension.size_32dp)
                )
                .padding(
                    vertical = Theme.dimension.size_12dp,
                    horizontal = Theme.dimension.size_16dp
                ),
        ) {
            Text("Kamu mendapatkan", style = UiFont.poppinsH5SemiBold)
            Text(fare.convertToRupiah(), style = UiFont.poppinsH5SemiBold.copy(color = UiColor.success500))
        }
        TemanFilledButton(
            content = "ke Beranda",
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = true,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            onNavigateHome()
        }
    }
}