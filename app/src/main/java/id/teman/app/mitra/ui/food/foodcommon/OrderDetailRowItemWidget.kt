package id.teman.app.mitra.ui.food.foodcommon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderItemSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun OrderDetailRowItemWidget(item: RestaurantOrderItemSpec) {
    Column {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = Theme.dimension.size_16dp,
                    vertical = Theme.dimension.size_8dp
                )
                .fillMaxWidth()
                .border(
                    border = BorderStroke(
                        width = Theme.dimension.size_1dp,
                        color = UiColor.neutral50
                    ),
                    shape = RoundedCornerShape(Theme.dimension.size_16dp)
                )
                .padding(Theme.dimension.size_16dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            val price =
                if (item.promoPrice > 0.0) item.promoPrice.convertToRupiah() else item.price.convertToRupiah()
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = UiFont.poppinsP2SemiBold)
                Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                Text(item.description, style = UiFont.poppinsCaptionMedium)
                Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
                Text("${item.quantity} Pcs", style = UiFont.poppinsCaptionMedium)
                Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
                Row {
                    Text(
                        price,
                        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.tertiaryBlue500)
                    )
                    if (item.promoPrice > 0.0) {
                        Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                        Text(
                            item.price.convertToRupiah(),
                            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral200),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(Theme.dimension.size_4dp),
                            color = Color.Transparent
                        ),
                    value = "Catatan : ${item.note.ifEmpty { "Tidak Ada" }}",
                    enabled = false,
                    placeholder = {},
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = UiColor.neutral100,
                        cursorColor = UiColor.black,
                        unfocusedBorderColor = UiColor.neutral100
                    ),
                    onValueChange = {},
                )
            }
            Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
            GlideImage(
                imageModel = item.photo,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(Theme.dimension.size_80dp)
                    .clip(shape = RoundedCornerShape(Theme.dimension.size_12dp))
            )
        }
    }
}