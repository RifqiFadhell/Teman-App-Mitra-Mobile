package id.teman.app.mitra.ui.registration.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun FormImageField(
    title: String,
    hint: String,
    @DrawableRes icon: Int? = null,
    imagePath: String = "",
    onClick: () -> Unit
) {
    val activeColor = if (imagePath.isNotNullOrEmpty()) UiColor.success500 else UiColor.neutral100
    val activeText = if (imagePath.isNotNullOrEmpty()) imagePath else hint
    Column(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_24dp
            )
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Text(title, style = UiFont.poppinsP2Medium)
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = BorderStroke(
                        width = Theme.dimension.size_1dp,
                        color = activeColor
                    ),
                    shape = RoundedCornerShape(Theme.dimension.size_4dp)
                )
                .padding(
                    horizontal = Theme.dimension.size_16dp,
                    vertical = Theme.dimension.size_14dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(activeText, style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400))
            if (icon != null) {
                GlideImage(
                    imageModel = icon,
                    modifier = Modifier.size(Theme.dimension.size_20dp),
                    imageOptions = ImageOptions(
                        colorFilter = if (imagePath.isNotNullOrEmpty()) ColorFilter.tint(
                            UiColor.success500
                        ) else null
                    )
                )
            }
        }
    }
}