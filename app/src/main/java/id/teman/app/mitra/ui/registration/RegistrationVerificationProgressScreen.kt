package id.teman.app.mitra.ui.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.ui.BasicTopNavigation
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun RegistrationVerificationProgressScreen(navigator: DestinationsNavigator, title: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        BasicTopNavigation(title = "Pendaftaran $title") {
            navigator.popBackStack()
        }

        GlideImage(
            imageModel = R.drawable.ic_verification_account,
            modifier = Modifier
                .padding(
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp,
                    top = Theme.dimension.size_48dp
                ).aspectRatio(2/1f),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit
            )
        )

        Spacer(modifier = Modifier.height(Theme.dimension.size_48dp))
        Text(
            "Akun Kamu sedang diverifikasi",
            style = UiFont.poppinsH3Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
        Text(
            "Kami akan hubungi Kamu jika akun Kamu sudah aktif. Setelah aktif, Kamu bisa langsung login dan terima order.",
            style = UiFont.poppinsSubHMedium.copy(color = UiColor.neutral600),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
        )

        Spacer(modifier = Modifier.weight(1f))
        TemanFilledButton(
            content = "Kembali Ke Beranda", buttonType = ButtonType.Large, activeColor =
            UiColor.primaryRed500, isEnabled = true,
            activeTextColor = UiColor.white,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Theme.dimension.size_16dp,
                    vertical = Theme.dimension.size_24dp
                )
        ) {
            navigator.popBackStack()
        }
    }
}