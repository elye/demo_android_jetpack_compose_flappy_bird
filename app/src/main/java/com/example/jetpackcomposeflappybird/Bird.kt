package com.example.jetpackcomposeflappybird

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Bird(coroutineScope: CoroutineScope, tiles: Bitmap, columns: Int = 1, rows: Int = 1) {

    private var yPosition = .5f
    private var ySpeed = 0f
    private var goingUp = false
    val bitmaps = mutableListOf<ImageBitmap>()

    init {
        coroutineScope.launch {
            val width = tiles.width / columns
            val height = tiles.height / rows
            for (r in 0 until rows) {
                for (c in 0 until columns) {
                    bitmaps.add(Bitmap.createBitmap(tiles, c * width, r * height, width, height).asImageBitmap())
                }
            }
        }
    }

    fun bounds(fieldRect: Rect): Rect {
        val height = fieldRect.height * size
        val h2 = height / 2
        val width = height * aspectRatio
        val w2 = width / 2
        val offset = Offset(fieldRect.center.x, fieldRect.top + h2 + (fieldRect.height - height) * yPosition)
        return Rect(left = offset.x - w2, top = offset.y - h2, right = offset.x + w2, bottom = offset.y + h2)
    }

    fun flap() {
        goingUp = true
    }

    fun move(time: Float) {
        yPosition += ySpeed * time
        ySpeed += time * if (goingUp) {
            -acceleration
        } else {
            acceleration
        }
        if (ySpeed < -maxSpeed) {
            ySpeed = -maxSpeed
            goingUp = false
        } else if (ySpeed > maxSpeed) {
            ySpeed = maxSpeed
        }
    }

    fun reset() {
        yPosition = .5f
        ySpeed = 0f
        goingUp = false
    }

    fun rotation(): Float = ySpeed * speedToDegrees

    companion object {
        const val acceleration = .04f
        const val aspectRatio = 1.4f
        const val maxSpeed = .06f
        const val size = .06f
        const val speedToDegrees = 500f
    }
}

@Composable
fun Bird(viewModel: GameViewModel = viewModel()) {
    val bird = viewModel.bird
    var frame by remember { mutableStateOf(0) }
    val frames = bird.bitmaps.size
    val target = if (viewModel.isRunning || viewModel.isEnding) 1f else 0f  // changing the value resets the animation
    val moveAnimation = viewModel.transition.animateFloat(
        initialValue = 0f,
        targetValue = target,
        animationSpec = infiniteRepeatable(tween(150, easing = LinearEasing))
    )
    if (viewModel.isRunning || viewModel.isEnding) {
        viewModel.moveBird(moveAnimation.value)
    }
    if (viewModel.isRunning) {
        val flapAnimation = viewModel.transition.animateFloat(
            initialValue = 0f,
            targetValue = frames.toFloat(),
            animationSpec = infiniteRepeatable(tween(200, easing = LinearEasing), repeatMode = RepeatMode.Reverse)
        )
        frame = flapAnimation.value.toInt()
    }
    val fieldRect = viewModel.fieldRect
    if (frames > 0 && fieldRect != null) {
        val birdRect = bird.bounds(fieldRect)
        viewModel.birdRect = birdRect
        Image(
            bitmap = bird.bitmaps[frame],
            contentDescription = null,
            modifier = Modifier
                .size(birdRect.width.dp, birdRect.height.dp)
                .offset(birdRect.topLeft.x.dp, birdRect.topLeft.y.dp)
                .rotate(bird.rotation()),
            contentScale = ContentScale.FillBounds,
        )
    }
    SideEffect {
        if (viewModel.isRunning) {
            viewModel.checkForCollision()
        }
        if (viewModel.isEnding) {
            viewModel.checkForEnd()
        }
    }
}

