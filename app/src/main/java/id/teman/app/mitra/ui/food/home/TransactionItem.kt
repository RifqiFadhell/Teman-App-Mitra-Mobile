package id.teman.app.mitra.ui.food.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import id.teman.app.mitra.R
import id.teman.app.mitra.common.CustomChip
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.domain.model.TransactionFilter
import id.teman.app.mitra.domain.model.chipBackgroundColor
import id.teman.app.mitra.domain.model.chipTextColor
import id.teman.app.mitra.domain.model.restaurant.RestaurantSummarySpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun TransactionItem(
    restaurant: RestaurantSummarySpec?, onChangeFilterCLick: (TransactionFilter) -> Unit,
    onDetailClick: (Double) -> Unit
) {
    var activeCategory by remember { mutableStateOf(TransactionFilter.ALL) }
    val income = restaurant?.income.orZero()
    val totalTransaction = restaurant?.transaction.orZero()
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_20dp,
            bottom = Theme.dimension.size_32dp
        )
    ) {
        LazyRow {
            itemsIndexed(TransactionFilter.values()) { index, item ->
                CustomChip(
                    title = item.title,
                    backgroundColor = (item == activeCategory).chipBackgroundColor,
                    textColor = (item == activeCategory).chipTextColor,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(
                            end = Theme.dimension.size_8dp,
                            start = if (index == 0) Theme.dimension.size_16dp else Theme.dimension.size_0dp
                        ),
                    textModifier = Modifier.padding(
                        vertical = Theme.dimension.size_8dp,
                        horizontal = Theme.dimension.size_12dp
                    )
                ) {
                    activeCategory = item
                    onChangeFilterCLick(item)
                }
            }
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
        LazyRow {
            items(2) { index ->
                TransactionCardItem(
                    modifier = Modifier.padding
                        (
                        start = if (index == 0) Theme.dimension.size_16dp else Theme.dimension.size_0dp,
                        end = Theme.dimension.size_10dp
                    ),
                    cardBackgroundColor = if (index == 0) UiColor.primaryRed500 else UiColor.primaryYellow500,
                    iconBackgroundColor = if (index == 0) UiColor.primaryRed50 else UiColor.primaryYellow50,
                    value = if (index == 0) income.convertToRupiah() else totalTransaction.orZero()
                        .toString(),
                    title = if (index == 0) "Pendapatan Total" else "Total Transaksi",
                    icon = if (index == 0) R.drawable.ic_dollar else R.drawable.ic_notes,
                    onDetailClick = {
                        onDetailClick(income)
                    }
                )
            }
        }
    }
}

@Composable
private fun TransactionCardItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    iconBackgroundColor: Color,
    cardBackgroundColor: Color,
    @DrawableRes icon: Int = R.drawable.ic_dollar,
    onDetailClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp
    Column(
        modifier = modifier
            .width((width * 0.65).dp)
            .background(
                color = cardBackgroundColor,
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_16dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TemanCircleButton(
                icon = icon,
                circleModifier = Modifier.size(Theme.dimension.size_50dp),
                iconModifier = Modifier.size(Theme.dimension.size_24dp),
                circleBackgroundColor = iconBackgroundColor,
                iconColor = cardBackgroundColor
            )
            Text(
                "Lihat Detail",
                modifier = Modifier
                    .background(
                        color = iconBackgroundColor,
                        shape = RoundedCornerShape(Theme.dimension.size_12dp)
                    )
                    .padding(
                        horizontal = Theme.dimension.size_12dp,
                        vertical = Theme.dimension.size_8dp
                    )
                    .clickable {
                        onDetailClick()
                    },
                style = UiFont.poppinsP2SemiBold.copy(color = cardBackgroundColor)
            )
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_40dp))
        Text(title, style = UiFont.poppinsP2SemiBold.copy(color = UiColor.white))
        Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
        Text(value, style = UiFont.poppinsH1Bold.copy(color = UiColor.white))
    }
}