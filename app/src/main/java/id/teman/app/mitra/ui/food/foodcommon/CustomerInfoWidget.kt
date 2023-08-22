package id.teman.app.mitra.ui.food.foodcommon

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.convertUtcIso8601ToLocalTimeAgo
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable fun CustomerInfoWidget(item: RestaurantOrderSpec) {
    Row(modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)) {
        GlideImage(
            imageModel = R.drawable.ic_person,
            modifier = Modifier
                .size(Theme.dimension.size_48dp)
                .clip(RoundedCornerShape(Theme.dimension.size_12dp))
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column(
            modifier = Modifier
                .padding(start = Theme.dimension.size_8dp)
                .weight(1f)
        ) {
            Text(
                item.userName,
                style = UiFont.poppinsP3SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
            Text(
                "${item.orderItems.count()} Menu • ${item.orderTime.convertUtcIso8601ToLocalTimeAgo()}",
                modifier = Modifier.padding(vertical = Theme.dimension.size_4dp),
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral900),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}