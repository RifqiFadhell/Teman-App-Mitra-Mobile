package id.teman.app.mitra.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun TextInputFieldIcon(
    title: String,
    textBox: String,
    placeholders: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier,
    onChange: (String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text(
            title,
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = placeholders)
            },
            value = value,
            keyboardOptions = keyboardOptions,
            onValueChange = {
                value = it
                onChange(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(TextFieldDefaults.MinHeight)
                        .background(color = UiColor.neutral100)
                ) {
                    Text(
                        textBox, style = UiFont.poppinsCaptionSemiBold,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }
}

@Composable
fun TextInputField(
    title: String,
    placeholders: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier,
    text: String = "",
    isEnabled: Boolean = true,
    onChange: (String) -> Unit
) {
    var value by remember { mutableStateOf(text) }
    Column(modifier = modifier) {
        Text(
            title,
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = placeholders)
            },
            value = value,
            keyboardOptions = keyboardOptions,
            onValueChange = {
                value = it
                onChange(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            enabled = isEnabled
        )
    }
}