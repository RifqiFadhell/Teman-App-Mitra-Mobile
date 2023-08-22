package id.teman.app.mitra.ui.food.foodcommon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.ResponsiveText
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun SearchWidget(query: String, onSearch: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_12dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
            .fillMaxWidth(),
        value = query,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        shape = RoundedCornerShape(Theme.dimension.size_30dp),
        onValueChange = { newValue ->
            onSearch(newValue)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = UiColor.neutral100,
            cursorColor = UiColor.black,
            unfocusedBorderColor = UiColor.neutral100
        ),
        placeholder = {
            ResponsiveText(
                text = "Cari order dengan nama customer...",
                textStyle = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
            )
        },
        leadingIcon = {
            GlideImage(
                R.drawable.ic_search,
                modifier = Modifier
                    .size(Theme.dimension.size_24dp)
                    .clickable {
                        onSearch(query)
                    }
            )
        }
    )
//    Box(
//        Modifier
//            .fillMaxWidth()
//            .padding(
//                top = Theme.dimension.size_12dp,
//                start = Theme.dimension.size_16dp,
//                end = Theme.dimension.size_16dp
//            )
//            .border(
//                width = Theme.dimension.size_2dp,
//                color = UiColor.neutral100,
//                shape = RoundedCornerShape(Theme.dimension.size_30dp)
//            )
//            .padding(
//                vertical = Theme.dimension.size_16dp,
//                horizontal = Theme.dimension.size_24dp
//            )
//            .noRippleClickable {
//
//            }
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            GlideImage(
//                R.drawable.ic_search,
//                modifier = Modifier.size(Theme.dimension.size_24dp)
//            )
//            Spacer(modifier = Modifier.width(Theme.dimension.size_12dp))
//            ResponsiveText(
//                "Cari order dengan nama customer...",
//                textStyle = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400),
//            )
//        }
//    }
}