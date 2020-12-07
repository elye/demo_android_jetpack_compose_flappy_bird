package com.example.jetpackcomposeflappybird

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.loadImageResource


@Composable
fun GameConsole(
    modifier: Modifier = Modifier,
    gameState: MutableState<GameState>,
    clickChange: MutableState<Boolean>,
    gameScore: MutableState<Int>
    ) {

    val bird = loadImageResource(id = R.drawable.flappy)
    val background = loadImageResource(id = R.drawable.background)
    val upPipe = loadImageResource(id = R.drawable.pipe_up)
    val downPipe = loadImageResource(id = R.drawable.pipe_down)
    val animatedFloat = animatedFloat(initVal = 0f)
    val birdState = remember(bird) { BirdState(bird) }
    val backgroundState = remember(background) { BackgroundState(background) }
    val pipesState = remember(upPipe, downPipe) { PipesState(upPipe, downPipe, gameScore) }

    remember(clickChange.value) { birdState.jump() }

    Canvas(modifier = modifier) {
        val gameRect = Rect(0f, 0f, size.width, size.height)

        if (animatedFloat.isRunning && gameState.value.isFinished()) {
            animatedFloat.stop()
        } else if (!animatedFloat.isRunning && gameState.value.isRunning()) {
            animatedFloat.snapTo(0f)
            animatedFloat.animateTo(
                targetValue = 1f,
                anim = repeatable(
                    iterations = AnimationConstants.Infinite,
                    animation = tween(durationMillis = 250, easing = LinearEasing),
                )
            )
            pipesState.init(this)
            birdState.init()
            gameScore.value = 0
        }
        val tick = animatedFloat.value

        if (gameState.value.isRunning()) {
            backgroundState.move()
            birdState.move()
            pipesState.move()
        } else if (gameState.value.isEnding()){
            birdState.dying()
        }

        backgroundState.draw(this, tick)
        val pipeRects = pipesState.draw(this, tick)
        val birdRect = birdState.draw(this, tick, gameState.value)

        if (gameState.value.isRunning()) {
            checkCollision(gameState, gameRect, pipeRects, birdRect)
        } else if (gameState.value.isEnding()) {
            finishFalling(gameState, gameRect, birdRect)
        }
    }
}

private fun finishFalling(
    animationStart: MutableState<GameState>,
    gameRect: Rect,
    birdRect: Rect) {
    if (!gameRect.overlaps(birdRect)) {
        animationStart.value = GameState(GameState.Status.STOPPED)
    }
}

private fun checkCollision(
    animationStart: MutableState<GameState>,
    gameRect: Rect,
    pipeRects: List<Rect>,
    birdRect: Rect) {
    if (!gameRect.overlaps(birdRect) || pipeRects.firstOrNull { it.overlaps(birdRect) } != null) {
        animationStart.value = GameState(GameState.Status.ENDING)
    }
}
