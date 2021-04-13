package com.example.jetpackcomposeflappybird

import android.view.MotionEvent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun GameConsole(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<GameViewModel>()
    viewModel.transition = rememberInfiniteTransition()
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .pointerInteropFilter {
                var used = false
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (viewModel.isRunning) {
                            viewModel.flap()
                            used = true
                        }
                    }
                }
                used
            }) {
        viewModel.fieldRect = Rect(0f, 0f, maxWidth.value, maxHeight.value)

        Background(viewModel)
        Pipes(viewModel)
        Bird(viewModel)
        Score(viewModel.gameScore, Modifier.fillMaxWidth())
        if (viewModel.isStopped) {
            Start(Modifier.fillMaxSize()) {
                viewModel.start()
            }
        }
    }
}

@Composable
private fun Score(gameScore: Int, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        fontSize = 48.sp,
        text = gameScore.toString(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun Start(
    modifier: Modifier = Modifier,
    onStart: () -> Unit
) {
    TextButton(modifier = modifier, onClick = {
        onStart()
    }) {
        Text(
            text = "\n\n\nClick to Start",
            fontSize = 32.sp
        )
    }
}

@Composable
fun Background(viewModel: GameViewModel = viewModel()) {
    val fieldRect = viewModel.fieldRect ?: return
    val image = painterResource(R.drawable.background)
    var fraction by remember { mutableStateOf(0f) }
    if (viewModel.isRunning) {
        val animation = viewModel.transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = 10000, easing = LinearEasing))
        )
        fraction = animation.value
    }
    val width = fieldRect.width.dp
    val currentPosX = -width * fraction
    val modifier = Modifier.size(width, fieldRect.height.dp)
    Image(painter = image, contentDescription = null, modifier = modifier.offset(x = currentPosX), contentScale = ContentScale.Crop)
    Image(painter = image, contentDescription = null, modifier = modifier.offset(x = currentPosX + width), contentScale = ContentScale.Crop)
}
