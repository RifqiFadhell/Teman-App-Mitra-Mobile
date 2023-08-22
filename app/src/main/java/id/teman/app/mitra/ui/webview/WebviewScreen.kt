package id.teman.app.mitra.ui.webview

import android.app.Activity
import android.content.Intent
import android.view.View.SCROLLBARS_INSIDE_OVERLAY
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.coreui.typography.UiColor

@Destination
@Composable
fun WebviewScreen(navigator: DestinationsNavigator, url: String) {
    var backEnabled by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val localContext = LocalContext.current
    val activity = localContext as Activity
    Scaffold(
        topBar = {},
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context)
                    },
                    update = { webview ->
                        webview.apply {
                            webViewClient = object: WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val requestUrl = request?.url.toString()
                                    return if (requestUrl.contains("gojek://")
                                        || requestUrl.contains("shopeeid://")
                                        || requestUrl.contains("//wsa.wallet.airpay.co.id/")

                                        // This is handle for sandbox Simulator
                                        || requestUrl.contains("/gopay/partner/")
                                        || requestUrl.contains("/shopeepay/")) {
                                        val intent = Intent(Intent.ACTION_VIEW, request?.url)
                                        activity.startActivity(intent)
                                        true
                                    } else {
                                        false
                                    }
                                }
                            }
                            with(settings) {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                scrollBarStyle = SCROLLBARS_INSIDE_OVERLAY
                                loadsImagesAutomatically = true
                            }
                            loadUrl(url)
                        }
                    }
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        }
    )
}