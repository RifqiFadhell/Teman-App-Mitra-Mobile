package id.teman.app.mitra.ui.transport.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun PaymentItemRow(titleText: String, valueText: String, textColor: Color = UiColor.neutral900, quantity: String = "", notes: String = "") {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            if (quantity.isNotEmpty()) {
                Text("$titleText - $quantity Pcs", style = UiFont.poppinsP2Medium)
            } else {
                Text(titleText, style = UiFont.poppinsP2Medium)
            }
            Text(valueText, style = UiFont.poppinsP2Medium.copy(color = textColor))
        }
        if (notes.isNotEmpty()) {
            Text("Catatan : $notes", style = UiFont.poppinsP2Medium)
        }
    }
    Spacer(Modifier.height(Theme.dimension.size_12dp))
}