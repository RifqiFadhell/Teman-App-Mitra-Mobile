package id.teman.app.mitra.ui.registration.driver

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
 fun SingleTextFormField(
    textFieldValue: String,
    title: String,
    errorMessage: String? = null,
    maxLength: Int = 256,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textInfo: String? = null,
    onTextChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_24dp
            )
            .fillMaxWidth()
    ) {
        Text(title, style = UiFont.poppinsP2Medium)
        Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
        OutlinedTextField(
            value = textFieldValue,
            isError = errorMessage.isNotNullOrEmpty(),
            placeholder = {
                Text("Isi $title", style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500))
            },
            shape = RoundedCornerShape(Theme.dimension.size_4dp),
            onValueChange = {
                if (it.length <= maxLength) onTextChanged(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            ),
            visualTransformation = visualTransformation
        )
        if (textInfo.isNotNullOrEmpty()) {
            Text(
                textInfo.orEmpty(),
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral100),
                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
            )
        }
        errorMessage?.let {
            Text(
                it,
                style = UiFont.poppinsCaptionMedium.copy(color = UiColor.primaryRed500),
                modifier = Modifier.padding(top = Theme.dimension.size_4dp)
            )
        }
    }
}