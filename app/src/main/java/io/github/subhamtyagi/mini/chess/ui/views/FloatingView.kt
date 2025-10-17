package io.github.subhamtyagi.mini.chess.ui.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.subhamtyagi.mini.chess.R


@Composable
fun DraggableResizableFloatingWindow(
    onClose: () -> Unit,
    onHide: () -> Unit

) {
    var windowWidth by remember { mutableStateOf(300.dp) }
    var windowHeight by remember { mutableStateOf(400.dp) }
    var offsetX by remember { mutableStateOf(2.dp) }
    var offsetY by remember { mutableStateOf(2.dp) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    offsetX.roundToPx(),
                    offsetY.roundToPx()
                )
            }
            .size(width = windowWidth, height = windowHeight)
            .background(Color.Black)
            .border(
                1.dp,
                Color.Gray,
                RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    dragOffsetX += dragAmount.x
                    dragOffsetY += dragAmount.y
                    change.consume()
                }
            }

    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.round_mini),
                contentDescription = "Hide Window",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp)
                    .clickable { onHide() }
            )

            Image(
                painter = painterResource(id = R.drawable.round_close),
                contentDescription = "Close Window",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp)
                    .clickable { onClose() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 8.dp, end = 8.dp, bottom = 24.dp)
        ) {

            WebViewScreen("https://lichess.org/analysis/")

        }
        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd)
                .alpha(0.5f)
                .background(Color.Gray, shape = CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->

                        with(density) {
                            windowWidth = (windowWidth + dragAmount.x.toDp()).coerceAtLeast(150.dp)
                            windowHeight =
                                (windowHeight + dragAmount.y.toDp()).coerceAtLeast(150.dp)
                        }

                    }
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_resize),
                contentDescription = "Resize",
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


