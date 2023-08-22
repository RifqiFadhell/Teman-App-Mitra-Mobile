package id.teman.app.mitra.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun TopBar(title: String, @DrawableRes icon: Int = R.drawable.ic_arrow_back, onClick: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.Transparent,
        contentPadding = PaddingValues(horizontal = Theme.dimension.size_16dp),
        modifier = Modifier.height(Theme.dimension.size_56dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            GlideImage(
                imageModel = icon,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(Theme.dimension.size_24dp)
                    .clickable {
                        onClick()
                    }
            )
            Text(
                title,
                style = UiFont.poppinsH5SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun RewardTopBar(title: String, @DrawableRes icon: Int = R.drawable.ic_arrow_back, onClick: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.Transparent,
        contentPadding = PaddingValues(horizontal = Theme.dimension.size_16dp),
        modifier = Modifier.height(Theme.dimension.size_56dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            GlideImage(
                imageModel = icon,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(Theme.dimension.size_24dp)
                    .clickable {
                        onClick()
                    },
                imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.white))
            )
            Text(
                title,
                style = UiFont.poppinsH5SemiBold,
                modifier = Modifier.align(Alignment.Center),
                color = UiColor.white
            )
        }
    }
}

@Composable
fun TopBarWallet(title: String, onBackClick: () -> Unit, onWalletClick: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.Transparent,
        contentPadding = PaddingValues(
            horizontal = Theme.dimension.size_16dp,
            vertical = Theme.dimension.size_8dp
        ),
        modifier = Modifier.height(Theme.dimension.size_56dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                GlideImage(
                    imageModel = R.drawable.ic_arrow_back,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(Theme.dimension.size_24dp)
                        .clickable {
                            onBackClick()
                        },
                    imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.white))
                )
                Text(
                    title,
                    textAlign = TextAlign.Start,
                    style = UiFont.poppinsH5SemiBold,
                    modifier = Modifier.padding(start = Theme.dimension.size_12dp),
                    color = UiColor.white
                )
            }
            Row(
                modifier = Modifier
                    .background(
                        Color.Transparent
                    )
                    .border(
                        border = BorderStroke(
                            width = Theme.dimension.size_1dp,
                            color = UiColor.neutral700
                        ), shape = RoundedCornerShape(Theme.dimension.size_24dp)
                    )
                    .padding(
                        vertical = Theme.dimension.size_8dp,
                        horizontal = Theme.dimension.size_12dp
                    )
                    .clickable {
                        onWalletClick()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                GlideImage(
                    R.drawable.ic_money_withdraw,
                    modifier = Modifier.size(Theme.dimension.size_16dp)
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                Text(
                    "Isi Kantong",
                    style = UiFont.poppinsCaptionSemiBold.copy(color = UiColor.success500)
                )
            }
        }
    }
}

@Composable
fun TopBarReferral(title: String, onBackClick: () -> Unit, onHistoryClick: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.Transparent,
        contentPadding = PaddingValues(
            horizontal = Theme.dimension.size_16dp,
            vertical = Theme.dimension.size_8dp
        ),
        modifier = Modifier.height(Theme.dimension.size_56dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                GlideImage(
                    imageModel = R.drawable.ic_arrow_back,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(Theme.dimension.size_24dp)
                        .clickable {
                            onBackClick()
                        }
                )
                Text(
                    title,
                    textAlign = TextAlign.Start,
                    style = UiFont.poppinsH5SemiBold,
                    modifier = Modifier.padding(start = Theme.dimension.size_12dp),
                    color = UiColor.black
                )
            }
            Row(
                modifier = Modifier
                    .background(
                        Color.Transparent
                    )
                    .border(
                        border = BorderStroke(
                            width = Theme.dimension.size_1dp,
                            color = UiColor.neutral700
                        ), shape = RoundedCornerShape(Theme.dimension.size_24dp)
                    )
                    .padding(
                        vertical = Theme.dimension.size_8dp,
                        horizontal = Theme.dimension.size_12dp
                    )
                    .clickable {
                        onHistoryClick()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                GlideImage(
                    R.drawable.ic_nav_bottom_order,
                    modifier = Modifier.size(Theme.dimension.size_16dp),
                    imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.black))
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                Text(
                    "Riwayat Referral",
                    style = UiFont.poppinsCaptionSemiBold.copy(color = UiColor.black)
                )
            }
        }
    }
}