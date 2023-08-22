package id.teman.app.mitra.dialog

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiFont

@Composable
fun RowScope.GeneralActionButton(
    text: String,
    textColor: Color,
    isFirstAction: Boolean,
    onClick: () -> Unit
) = OutlinedButton(
    modifier = Modifier.weight(1f).wrapContentHeight(),
    shape = RoundedCornerShape(
        bottomStart = if (isFirstAction) Theme.dimension.size_14dp else Theme.dimension.size_0dp,
        bottomEnd = if (isFirstAction) Theme.dimension.size_0dp else Theme.dimension.size_14dp
    ),
    elevation = ButtonDefaults.elevation(
        defaultElevation = Theme.dimension.size_0dp
    ),
    content = {
        Text(
            text,
            style = UiFont.poppinsH6Medium.copy(color = textColor),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    onClick = onClick
)