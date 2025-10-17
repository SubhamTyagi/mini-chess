package io.github.subhamtyagi.mini.chess.ui.views

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import io.github.subhamtyagi.mini.chess.jsRemoveElements
import io.github.subhamtyagi.mini.chess.ui.theme.ChessAnalysisMiniTheme
import io.github.subhamtyagi.mini.chess.utils.applyWebViewSettings
import io.github.subhamtyagi.mini.chess.utils.getHeader



@Composable
fun WebViewScreen(
    url: String, modifier: Modifier = Modifier
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    AndroidView(
        modifier = modifier, factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                applyWebViewSettings(this)
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.postDelayed({
                            view.evaluateJavascript(jsRemoveElements, null)
                        }, 100)
                    }
                }
                loadUrl(url, getHeader(settings))
                webView = this
            }
        },

        update = {
            it.loadUrl(url)
        })

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

@Preview(showBackground = true)
@Composable
fun WebViewPreview() {
    ChessAnalysisMiniTheme {
        WebViewScreen("https://lichess.org/analysis/")

    }
}