package io.github.subhamtyagi.mini.chess.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView

enum class Size {
    SMALL,
    MEDIUM,
    LARGE
}

@SuppressLint("SetJavaScriptEnabled")
fun applyWebViewSettings(view: WebView) {
    view.apply {
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.databaseEnabled = true

        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        settings.allowContentAccess = true
        settings.allowFileAccess = true

        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        settings.mediaPlaybackRequiresUserGesture = true

        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
        settings.blockNetworkLoads = false

        isFocusable = true
        isFocusableInTouchMode = true
        isLongClickable = true
        isHapticFeedbackEnabled = true
        //setInitialScale(50)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView.setWebContentsDebuggingEnabled(true)

        try {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } catch (e: Exception) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

    }
}

fun getHeader(settings: WebSettings): HashMap<String, String> {
    val headers = HashMap<String, String>()
    headers["User-Agent"] = settings.userAgentString
    headers["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
    headers["Accept-Language"] = "en-US,en;q=0.5"
    headers["Content-Security-Policy"] = generateCSPHeader()
    return headers
}

fun generateCSPHeader(): String {
    return "default-src * 'unsafe-inline' 'unsafe-eval'; " + "script-src * 'unsafe-inline' 'unsafe-eval'; " + "style-src * 'unsafe-inline'; " + "img-src * data: https:; " + "font-src * data:; " + "connect-src * ws: wss:; " + "connect-src *; " + "media-src *; " + "object-src *; " + "child-src *; " + "frame-ancestors *; " + "base-uri *; " + "form-action *;"
}


fun Context.getScreenWidthAndHeight(size: Size): Pair<Int, Int> {
    val displayMetrics = resources.displayMetrics
    return when (size) {
        Size.SMALL -> Pair(
            (displayMetrics.widthPixels * 0.4).toInt(),
            (displayMetrics.heightPixels * 0.3).toInt()
        )

        Size.MEDIUM -> Pair(
            (displayMetrics.widthPixels * 0.5).toInt(),
            (displayMetrics.heightPixels * 0.4).toInt()
        )

        Size.LARGE -> Pair(
            (displayMetrics.widthPixels * 0.7).toInt(),
            (displayMetrics.heightPixels * 0.6).toInt()
        )

    }
}


fun createLayoutParams(baseContext: Context, windowSize: Size): WindowManager.LayoutParams {
    val (width, height) = baseContext.getScreenWidthAndHeight(windowSize)
    val params = WindowManager.LayoutParams(
        width,
        height,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        x = 0
        y = 0
        // x = 100 + (nextWindowId * 30)
        //y = 100 + (nextWindowId * 30)
    }
    params.flags = params.flags or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
    params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
    return params
}
