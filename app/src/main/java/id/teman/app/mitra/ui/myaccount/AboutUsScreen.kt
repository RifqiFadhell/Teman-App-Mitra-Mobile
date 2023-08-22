package id.teman.app.mitra.ui.myaccount

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.common.CenterTopAppBar
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import id.teman.app.mitra.R as rAppModule

val caption =
    "Aplikasi penyedia layanan transportasi, pesan antar makanan, logistik, pembayaran, dan kebutuhan sehari-hari mulai dari makan dan minuman , perjalanan , edukasi, berita dan produk lainnya."

@Destination
@Composable
fun AboutUsScreen(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val call = Intent(Intent.ACTION_CALL, Uri.parse("tel:081231233697"))
            context.startActivity(call)
        } else {
            // Permission Denied: Do something
        }
    }
    Scaffold(
        topBar = {
            CenterTopAppBar(
                elevation = Theme.dimension.size_0dp,
                backgroundColor = Color.Transparent,
                title = {
                    Text(text = "Tentang", textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
            )
        },
        content = {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(
                    vertical = Theme.dimension.size_24dp,
                    horizontal = Theme.dimension.size_16dp
                )
            ) {
                GlideImage(
                    modifier = Modifier.aspectRatio(16f / 9f),
                    imageModel = rAppModule.drawable.ic_revamped_teman,
                    imageOptions = ImageOptions(contentScale = ContentScale.Fit)
                )
                Text(
                    "Tentang Aplikasi Teman",
                    style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier.padding(top = Theme.dimension.size_24dp)
                )
                Text(
                    caption,
                    style = UiFont.poppinsP1Medium,
                    modifier = Modifier.padding(top = Theme.dimension.size_12dp)
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
                ContactUsItem(rAppModule.drawable.email, "csinfo.teman@temanofficial.tech") {
                    val selectorIntent = Intent(Intent.ACTION_SENDTO)
                    selectorIntent.data = Uri.parse("mailto:")
                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf("csinfo.teman@temanofficial.tech")
                    )
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                    emailIntent.putExtra(Intent.EXTRA_TEXT, R.attr.text)
                    emailIntent.selector = selectorIntent
                    context.startActivity(
                        Intent.createChooser(
                            emailIntent,
                            "Mengirim Feedback Teman Mitra App"
                        )
                    )
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                ContactUsItem(rAppModule.drawable.web, "https://temanofficial.co.id") {
                    val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://temanofficial.co.id"))
                    context.startActivity(web)
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                ContactUsItem(rAppModule.drawable.whatsapp, "081231233697") {
                    val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281231233697"))
                    context.startActivity(web)
                }
                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                ContactUsItem(rAppModule.drawable.call, "02174792609") {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) -> {
                            // Some works that require permission
                            val call = Intent(Intent.ACTION_CALL, Uri.parse("tel:02174792609"))
                            context.startActivity(call)
                        }

                        else -> {
                            launcher.launch(Manifest.permission.CALL_PHONE)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ContactUsItem(
    icon: Int, title: String, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        TemanCircleButton(
            icon = icon,
            iconColor = UiColor.tertiaryBlue900,
            circleBackgroundColor = UiColor.tertiaryBlue50,
            circleModifier = Modifier.size(Theme.dimension.size_40dp),
            iconModifier = Modifier.size(Theme.dimension.size_20dp)
        )
        Text(
            title,
            modifier = Modifier.padding(start = Theme.dimension.size_12dp),
            style = UiFont.poppinsCaptionSemiBold
        )
    }
}

