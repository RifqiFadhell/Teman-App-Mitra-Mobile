package id.teman.app.mitra.ui.food.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextDecoration
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.convertToRupiah
import id.teman.app.mitra.domain.model.restaurant.RestaurantMenuSpec
import id.teman.app.mitra.ui.food.menu.domain.MenuSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun MenuListWidget(
    modifier: Modifier = Modifier,
    uiModel: MenuSpec,
    onChangeGroupName: (MenuSpec) -> Unit,
    onSwitchChanged: (Boolean, spec: RestaurantMenuSpec) -> Unit,
    onMenuClick: (RestaurantMenuSpec) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = modifier
            .border(BorderStroke(Theme.dimension.size_1dp, UiColor.neutral50))
            .background(
                color = UiColor.white,
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_16dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(uiModel.menuGroupName, style = UiFont.poppinsH5SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    onChangeGroupName(uiModel)
                }) {
                    Text(
                        "Ubah nama grup",
                        style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500)
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                    GlideImage(
                        imageModel = R.drawable.ic_edit,
                        modifier = Modifier.size(Theme.dimension.size_16dp)
                    )
                }
            }
            Row {
                Text(
                    "${uiModel.totalMenu} menu", style = UiFont.poppinsSubHMedium.copy(
                        color = UiColor.neutral300
                    )
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_14dp))
                GlideImage(
                    imageModel = R.drawable.ic_arrow_down,
                    modifier = Modifier
                        .size(Theme.dimension.size_24dp)
                        .rotate(rotateState)
                        .clickable {
                            isExpanded = !isExpanded
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))

        if (isExpanded) {
            uiModel.menus.forEachIndexed { index, menu ->
                var isActive by remember(menu.id) { mutableStateOf(menu.isActive) }
                val switchColor = if (isActive) UiColor.blue else UiColor.white

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = Theme.dimension.size_16dp).clickable { onMenuClick(menu) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(menu.name, style = UiFont.poppinsP2Medium)
                            Spacer(modifier = Modifier.padding(top = Theme.dimension.size_8dp))
                            if (menu.isPromo) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        menu.promoPrice.convertToRupiah(),
                                        style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral900)
                                    )
                                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                                    Text(
                                        menu.price.convertToRupiah(),
                                        style = UiFont.poppinsP2Medium.copy(
                                            color = UiColor.neutral300,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    )
                                }
                            } else {
                                Text(menu.price.convertToRupiah(), style = UiFont.poppinsP2Medium)
                            }
                        }
                        Switch(
                            checked = isActive,
                            onCheckedChange = {
                                isActive = it
                                onSwitchChanged(it, menu)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = switchColor,
                                checkedTrackColor = UiColor.tertiaryBlue50,
                                uncheckedThumbColor = UiColor.neutral100
                            )
                        )
                    }

                    if (index < uiModel.menus.size - 1) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Theme.dimension.size_16dp),
                            color = UiColor.neutral100,
                            thickness = Theme.dimension.size_1dp
                        )
                    }
                }
            }
        }
    }
}