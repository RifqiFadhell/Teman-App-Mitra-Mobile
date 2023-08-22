package id.teman.app.mitra.ui.registration.driver

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.toSize
import id.teman.app.mitra.common.noRippleClickable
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import java.util.Calendar

@Composable
 fun YearTextFormField(value: String = "", onSelected: (String) -> Unit) {
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val currentYearList = remember { mutableListOf<String>() }
    LaunchedEffect(key1 = true, block = {
        for (i in currentYear - 20..currentYear) {
            currentYearList.add("$i")
        }
    })
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    var textFiledSize by remember { mutableStateOf(Size.Zero) }
    Column(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_24dp
            )
            .fillMaxWidth()
    ) {
        Text("Isi Tahun", style = UiFont.poppinsP2Medium)
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        OutlinedTextField(
            value = value,
            enabled = false,
            placeholder = {
                Text("Isi Tahun", style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500))
            },
            shape = RoundedCornerShape(Theme.dimension.size_4dp),
            onValueChange = {
                // no op
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100,
                disabledBorderColor = UiColor.neutral100
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFiledSize = coordinates.size.toSize()
                }
                .noRippleClickable { expanded = !expanded },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            trailingIcon = {
                Icon(icon, "", Modifier.clickable {
                    expanded = !expanded
                })
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }, modifier = Modifier.width(with(LocalDensity.current) { textFiledSize.width.toDp() })) {
            currentYearList.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                ) {
                    Text(item)
                }
            }
        }
    }
}