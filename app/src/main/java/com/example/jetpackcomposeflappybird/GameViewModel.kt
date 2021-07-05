package com.example.jetpackcomposeflappybird

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    lateinit var transition: InfiniteTransition
    lateinit var bird: Bird
    private var gameState by mutableStateOf(GameState.STOPPED)
    private val lastBirdTime = mutableStateOf(0f)
    private val lastPipeTime = mutableStateOf(0f)
    var gameScore by mutableStateOf(0)
    val pipes = mutableListOf<Pipe>()
    var birdRect: Rect? = null
    var fieldRect: Rect? = null
    var pipeRects: List<Rect>? = null

    val isRunning get() = gameState == GameState.RUNNING
    val isEnding get() = gameState == GameState.ENDING
    val isStopped get() = gameState == GameState.STOPPED

    private fun addPipe() {
        val offset = (pipes.lastOrNull()?.offset ?: .6f) + .6f
        pipes.add(Pipe(offset))
    }

    fun checkForCollision() {
        val birdRect = birdRect ?: return
        val fieldRect = fieldRect ?: return
        val pipeRects = pipeRects ?: return
        val fieldCollision = birdRect.bottom < fieldRect.top || birdRect.top > fieldRect.bottom
        val pipeCollision = pipeRects.any { pipe ->
            pipe.overlaps(birdRect)
        }
        if (fieldCollision || pipeCollision) {
            gameState = GameState.ENDING
        }
    }

    fun checkForEnd() {
        val birdRect = birdRect ?: return
        val fieldRect = fieldRect ?: return
        if (!birdRect.overlaps(fieldRect)) {
            gameState = GameState.STOPPED
        }
    }

    private fun elapsed(animationTime: Float, lastTimeState: MutableState<Float>): Float {
        val lastTime = lastTimeState.value
        lastTimeState.value = animationTime
        return if (animationTime < lastTime) {
            animationTime - lastTime + 1f
        } else {
            animationTime - lastTime
        }
    }

    fun flap() = bird.flap()

    fun moveBird(animationTime: Float) {
        val speed = if (isEnding) 2.5f else 1f
        val time = elapsed(animationTime, lastBirdTime) * speed
        bird.move(time)
    }

    fun movePipes(animationTime: Float) {
        val time = elapsed(animationTime, lastPipeTime)
        var removeFirst = false
        var addMore = false
        pipes.forEach { pipe ->
            pipe.move(time, onLost = {
                removeFirst = true
            }, onScore = {
                gameScore++
                addMore = true
            })
        }
        if (removeFirst) {
            pipes.removeAt(0)
        }
        if (addMore) {
            addPipe()
        }
    }

    fun start() {
        gameState = GameState.RUNNING
        gameScore = 0
        bird.reset()
        pipes.clear()
        addPipe()
        addPipe()
        lastBirdTime.value = 0f
        lastPipeTime.value = 0f
    }

    companion object {
        const val gapScale = .3f
    }
}
