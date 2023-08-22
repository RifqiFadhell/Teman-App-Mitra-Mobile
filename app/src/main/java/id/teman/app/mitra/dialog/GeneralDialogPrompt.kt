package id.teman.app.mitra.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun GeneralDialogPrompt(
    title: String,
    subtitle: String,
    dismissible: Boolean = true,
    actionButtons: @Composable RowScope.() -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible
        ),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    UiColor.neutralGray0,
                    shape = RoundedCornerShape(Theme.dimension.size_14dp)
                )
        ) {
            Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
            Text(
                title,
                style = UiFont.poppinsH5SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.dimension.size_16dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = Theme.dimension.size_2dp))
            Text(
                subtitle,
                style = UiFont.poppinsP1Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Theme.dimension.size_16dp)
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Theme.dimension.size_40dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                actionButtons()
            }
        }
    }
}

/**
 * Example of using these
 */
@Preview
@Composable
fun previewGeneralDialogPromptWith2ActionButtons() {
    GeneralDialogPrompt(
        title = "Yakin Mau Logout?",
        subtitle = "Kamu nanti harus masukin nomor handphone lagi untuk masuk",
        actionButtons = {
            GeneralActionButton(
                text = "sample_text_1",
                textColor = UiColor.primaryRed500,
                isFirstAction = true,
                onClick = {
                    // put action click here
                }
            )
            GeneralActionButton(
                text = "sample_text_2",
                textColor = UiColor.black,
                isFirstAction = false,
                onClick = {
                    // put action click here
                }
            )
        },
        onDismissRequest = {
            // dismiss action here
        }
    )
}