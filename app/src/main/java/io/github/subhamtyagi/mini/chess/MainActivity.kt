package io.github.subhamtyagi.mini.chess

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.github.subhamtyagi.mini.chess.ui.theme.ChessAnalysisMiniTheme
import io.github.subhamtyagi.mini.chess.ui.views.WebViewScreen
import io.github.subhamtyagi.mini.chess.utils.Size
import io.github.subhamtyagi.mini.chess.utils.createLayoutParams


class MainActivity : AppCompatActivity() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var params: WindowManager.LayoutParams
    private val CHANNEL_ID = "floating_window_channel"
    private val NOTIFICATION_ID = 9587

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        setContent {
            ChessAnalysisMiniTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var windowSize by rememberSaveable { mutableStateOf(Size.MEDIUM) }
                    var showFloatingWindow by rememberSaveable { mutableStateOf(false) }

                    FloatingWindowSizeSelector(
                        selectedOption = windowSize,
                        onOptionSelected = {
                            windowSize = it
                            showFloatingWindow = false
                        },
                        onConfirmClick = {
                            if (Settings.canDrawOverlays(this)) {
                                showFloatingWindow = true

                            } else {
                                requestOverlayPermission()
                            }

                        }
                    )
                    if (showFloatingWindow) {
                        showFloatingWindow(windowSize)
                    }
                }
            }
        }
    }

    private fun showFloatingWindow(windowSize: Size) {
        floatingView = layoutInflater.inflate(R.layout.floating_activity, null)
        floatingView.setViewTreeLifecycleOwner(this@MainActivity)
        floatingView.setViewTreeViewModelStoreOwner(this@MainActivity)
        floatingView.setViewTreeSavedStateRegistryOwner(this@MainActivity)
        val closeButton = floatingView.findViewById<ImageView>(R.id.close_window)
        val hideButton = floatingView.findViewById<ImageView>(R.id.hide_window)
        val resizeHandler = floatingView.findViewById<ImageView>(R.id.resizeHandler)
        val moveHandler = floatingView.findViewById<RelativeLayout>(R.id.floating_view_header)
        val gameView = floatingView.findViewById<ComposeView>(R.id.game_view)

        gameView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(this@MainActivity)
            setViewTreeViewModelStoreOwner(this@MainActivity)
            setViewTreeSavedStateRegistryOwner(this@MainActivity)

            setContent {
                ChessAnalysisMiniTheme {
                    WebViewScreen("https://lichess.org/analysis/")
                }
            }
        }

        params = createLayoutParams(baseContext, windowSize)
        windowManager.addView(floatingView, params)

        closeButton.setOnClickListener {
            windowManager.removeViewImmediate(floatingView)

        }

        hideButton.setOnClickListener {
            windowManager.removeView(floatingView)
            showNotification()
        }

        setupDragListener(viewHandler = moveHandler)
        setupResizeListener(
            resizeHandler, floatingView
        )
    }


    private fun showNotification() {
        var flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Tap to return to the Mini Chess")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
        notificationIntent.putExtra("FROM_NOTIFICATION", true)
        contentIntent.run {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            send()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Window",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupResizeListener(
        resizeHandle: View,
        rootView: View
    ) {
        resizeHandle.setOnTouchListener(object : View.OnTouchListener {
            private var initialWidth = 0
            private var initialHeight = 0
            private var initialX = 0f
            private var initialY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialWidth = params.width
                        initialHeight = params.height
                        initialX = event.rawX
                        initialY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialX).toInt()
                        val dy = (event.rawY - initialY).toInt()
                        params.width = (initialWidth + dx).coerceAtLeast(300)
                        params.height = (initialHeight + dy).coerceAtLeast(300)
                        windowManager.updateViewLayout(rootView, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val xDiff = (event.rawX - initialX).toInt()
                        val yDiff = (event.rawY - initialY).toInt()
                        if (xDiff < 10 && yDiff < 10) {
                            //Toast.makeText(this@MainActivity, "Clicked!", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragListener(
        viewHandler: View
    ) {
        viewHandler.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        val xDiff = (event.rawX - initialTouchX).toInt()
                        val yDiff = (event.rawY - initialTouchY).toInt()
                        if (xDiff < 10 && yDiff < 10) {
                            Toast.makeText(this@MainActivity, "Moved", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeViewImmediate(floatingView)
        }
    }

    private fun requestOverlayPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri())
        startActivity(intent)
        return
    }
}

@Composable
@Preview(showBackground = true)
fun DefaultPreview() {
    ChessAnalysisMiniTheme {
        var selectedOption by rememberSaveable { mutableStateOf(Size.MEDIUM) }
        FloatingWindowSizeSelector(
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it },
            onConfirmClick = {
                Log.d("FloatingWindow", "Selected: $selectedOption")
            }
        )
    }
}
