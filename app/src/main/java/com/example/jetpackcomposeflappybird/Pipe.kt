package com.example.jetpackcomposeflappybird

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class Pipe(var offset: Float) {

    private val gapPosition = gapRange.random() * .01f
    private var scored = false

    fun bounds(fieldRect: Rect): Pair<Rect, Rect> {
        val pipeX = offset * fieldRect.width
        val gapShift = fieldRect.height * GameViewModel.gapScale / 2
        val pipeShift = fieldRect.height * gapPosition
        val pipeSize = Size(fieldRect.width * scaleH, fieldRect.height * scaleV)
        return Pair(
            Rect(Offset(pipeX, pipeShift - gapShift - pipeSize.height), pipeSize),
            Rect(Offset(pipeX, pipeShift + gapShift), pipeSize)
        )
    }

    fun move(time: Float, onScore: () -> Unit, onLost: (Pipe) -> Unit) {
        offset -= time
        Log.d("Pipe", "time $time offset $offset")
        if (offset < -.1f) {
            onLost(this)
        } else if (offset < .4 && !scored) {
            onScore()
            scored = true
        }
    }

    companion object {
        val gapRange = 20..80  // in percent
        const val scaleH = .1f
        const val scaleV = .9f
    }
}

@Composable
fun Pipes(viewModel: GameViewModel = viewModel()) {
    val target = if (viewModel.isRunning) 1f else 0f    // changing the value resets the animation
    val animation = viewModel.transition.animateFloat(
        initialValue = 0f,
        targetValue = target,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing))
    )
    if (viewModel.isRunning) {
        viewModel.movePipes(animation.value)
    }
    val pipeRects = viewModel.pipes.mapNotNull { pipe ->
        pipePair(pipe, viewModel)
    }
    viewModel.pipeRects = pipeRects.flatMap { listOf(it.first, it.second) }
    pipeRects.forEach { PipePair(it) }
}

private fun pipePair(pipe: Pipe, viewModel: GameViewModel): Pair<Rect, Rect>? {
    val fieldRect = viewModel.fieldRect ?: return null
    return pipe.bounds(fieldRect)
}

@Composable
private fun PipePair(bounds: Pair<Rect, Rect>) {
    val upPipe = painterResource(R.drawable.pipe_up)
    val downPipe = painterResource(R.drawable.pipe_down)
    Image(
        painter = upPipe,
        contentDescription = null,
        modifier = Modifier
            .size(bounds.first.width.dp, bounds.first.height.dp)
            .offset(bounds.first.left.dp, bounds.first.top.dp),
        contentScale = ContentScale.FillBounds
    )
    Image(
        painter = downPipe,
        contentDescription = null,
        modifier = Modifier
            .size(bounds.second.width.dp, bounds.second.height.dp)
            .offset(bounds.second.left.dp, bounds.second.top.dp),
        contentScale = ContentScale.FillBounds
    )
}
