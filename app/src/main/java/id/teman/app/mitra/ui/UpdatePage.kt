package id.teman.app.mitra.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ButtonType
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdatePage(onClick: () -> Unit) {
    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                GlideImage(
                    imageModel = R.drawable.ic_notification,
                    modifier = Modifier.padding(
                        top = Theme.dimension.size_24dp)
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally),
                    imageOptions = ImageOptions(contentScale = ContentScale.Fit)
                )
                Text(
                    text = "Dapatkan pengalaman yang lebih baik dan fitur terbaru Teman App",
                    modifier = Modifier
                        .padding(
                            top = Theme.dimension.size_16dp,
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp
                        )
                        .align(Alignment.CenterHorizontally),
                    style = UiFont.cabinH3sSemiBold,
                    textAlign = TextAlign.Center
                )
                GlideImage(
                    imageModel = R.drawable.ic_update,
                    modifier = Modifier
                        .fillMaxWidth().padding(Theme.dimension.size_16dp)
                        .size(237.dp),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                )
                Text(
                    modifier = Modifier.padding(
                        horizontal = Theme.dimension.size_36dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                    text = "Kita punya update terbaru loh! Banyak perbaikan yang udah dilakuin. Yuk segera perbarui aplikasi untuk mendapatkan pengalaman yang lebih baik.",
                    style = UiFont.poppinsP3Medium,
                    textAlign = TextAlign.Center
                )
            }
        },
        bottomBar = {
            TemanFilledButton(
                content = "Perbarui Sekarang",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = Color.White,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_24dp
                    ),
                onClicked = {
                    onClick()
                })
        })
}