package id.teman.app.mitra.ui.food.foodcommon

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun EmptyRestaurantOrder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.dimension.size_16dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
        GlideImage(
            imageModel = R.drawable.empty_retaurant_order,
            modifier = Modifier
                .size(Theme.dimension.size_160dp)
                .align(Alignment.CenterHorizontally),
            imageOptions = ImageOptions(alignment = Alignment.Center)
        )
        Text(
            "Data Transaksi Kosong", style = UiFont.poppinsP3SemiBold, modifier = Modifier.padding(
                top = Theme.dimension.size_18dp, bottom = Theme.dimension.size_12dp
            )
        )
        Text(
            "Sepertinya kamu tidak memiliki transaksi",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
        )
    }
}