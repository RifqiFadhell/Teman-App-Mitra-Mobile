package id.teman.app.mitra.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.teman.app.mitra.R
import id.teman.app.mitra.domain.model.location.SearchLocationSpec
import id.teman.app.mitra.ui.theme.Theme
import id.teman.app.mitra.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun SearchDestinationSectionItem(item: SearchLocationSpec, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .background(color = UiColor.white)
            .padding(
                top = Theme.dimension.size_24dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
            .clickable {
                onClick(item.placeId)
            }
    ) {
        TemanCircleButton(
            icon = R.drawable.ic_search_location,
            iconModifier = Modifier
                .width(Theme.dimension.size_18dp)
                .height(Theme.dimension.size_22dp),
            circleBackgroundColor = UiColor.tertiaryBlue50,
            circleModifier = Modifier.size(Theme.dimension.size_52dp)
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
        Column {
            Text(
                item.title,
                style = UiFont.poppinsP3SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
            Text(
                item.description,
                style = UiFont.poppinsCaptionSemiBold
                    .copy(
                        color = UiColor.neutral400,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_18dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = UiColor.neutral100)
            )
        }
    }
}