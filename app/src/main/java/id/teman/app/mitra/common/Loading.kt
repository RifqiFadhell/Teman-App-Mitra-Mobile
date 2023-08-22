package id.teman.app.mitra.common

import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.widget.ConstraintLayout

@Composable
fun CustomLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(factory = { context ->
            ConstraintLayout(context).apply {
                val loading = ComposeView(context).apply {
                    setContent {
                        CircularProgressIndicator()
                    }
                }
                addView(
                    loading,
                    ViewGroup.LayoutParams(
                        dpToPx(context.resources, 50),
                        dpToPx(context.resources, 50)
                    )
                )
            }
        })
    }
}

@Composable
fun ShowLoading(isShow: Boolean) {
    if (isShow) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            CustomLoading(modifier = Modifier.fillMaxSize())
        }
    }
}