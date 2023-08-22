package id.teman.app.mitra.ui.food.foodcommon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.convertUtcIso8601ToLocalTimeAgo
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.domain.model.restaurant.colorStatus
import id.teman.app.mitra.domain.model.restaurant.textValue
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun FoodSingleOrderRowItemWidget(
    item: RestaurantOrderSpec,
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onDetailClick: (RestaurantOrderSpec) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(Theme.dimension.size_16dp)
            .border(
                border = BorderStroke(
                    width = Theme.dimension.size_1dp,
                    color = if (isSelected) UiColor.tertiaryBlue500 else UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_16dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongClick()
                    },
                    onTap = {
                        onDetailClick(item)
                    }
                )
            }
    ) {
        Row {
            GlideImage(
                imageModel = item.userPhoto,
                modifier = Modifier
                    .size(Theme.dimension.size_48dp)
                    .clip(RoundedCornerShape(Theme.dimension.size_12dp)),
                failure = {
                    painterResource(id = R.drawable.ic_person)
                },
                loading = {
                    painterResource(id = R.drawable.ic_person)
                }
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
                Text(
                    item.productName,
                    modifier = Modifier.padding(vertical = Theme.dimension.size_4dp),
                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral900),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${item.orderItems.count()} Pcs • ${item.orderTime.convertUtcIso8601ToLocalTimeAgo()}",
                    style = UiFont.poppinsCaptionMedium.copy(
                        color = UiColor.neutral300,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_12dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.totalPrice.convertToRupiah(),
                        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.tertiaryBlue500)
                    )
                    Text(
                        "• ${item.orderStatus.textValue()}",
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(Theme.dimension.size_32dp),
                                color = item.orderStatus.colorStatus().second
                            )
                            .padding(
                                vertical = Theme.dimension.size_4dp,
                                horizontal = Theme.dimension.size_12dp
                            ),
                        style = UiFont.poppinsCaptionMedium.copy(color = item.orderStatus.colorStatus().first)
                    )
                }
            }
        }
    }
}