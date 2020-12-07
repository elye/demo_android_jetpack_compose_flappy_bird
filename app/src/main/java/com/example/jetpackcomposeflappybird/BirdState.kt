package com.example.jetpackcomposeflappybird

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.DeferredResource
import androidx.compose.ui.unit.*

data class BirdState(val image: DeferredResource<ImageBitmap>) {
    private val imageWidth = 76
    private val imageHeight = 53
    private val velocityUnit = 3
    private val maxUpVelocity = velocityUnit * 6
    private val maxDownVelocity = velocityUnit * -6
    private val fallDownVelocity = velocityUnit * -9
    private var isGoingUp = false
    private var currentPosYOffset = 0
    private var upVelocity = 0

    fun init() {
        isGoingUp = false
        currentPosYOffset = 0
        upVelocity = 0
    }

    fun move() {
        if (isGoingUp) {
            if (upVelocity > maxUpVelocity) {
                isGoingUp = false
            } else {
                upVelocity += velocityUnit
            }
        } else {
            upVelocity -= velocityUnit

            if (upVelocity < maxDownVelocity) {
                upVelocity = maxDownVelocity
            }
        }
    }

    fun draw(drawScope: DrawScope,
             timeTick: Float,
             gameState: GameState
    ): Rect {
        val value = if (gameState.isEnding()) 1
        else if (timeTick < 0.25) 0
        else if (timeTick >= 0.5 && timeTick < 0.75) 2
        else 1

        return drawScope.draw(value, gameState)
    }

    private fun DrawScope.draw(value: Int, gameState: GameState): Rect {
        currentPosYOffset += upVelocity

        val center = IntOffset(
            (midX().toInt() - imageWidth.dp.toIntPx() / 2),
            (midY().toInt() - currentPosYOffset - imageHeight.dp.toIntPx() / 2)
        )

        val rotateCenter = IntOffset(
            midX().toInt(), midY().toInt() - currentPosYOffset
        )

        // Straight down if ending.
        val birdRotate = if (gameState.isEnding()) -90 else upVelocity

        withTransform({ rotate(-birdRotate.toFloat(), rotateCenter.toOffset()) }, {
            image.resource.resource?.let {
                drawImage(
                    image = it,
                    srcOffset = IntOffset((imageWidth * value).dp.toIntPx(), 0),
                    srcSize = IntSize(imageWidth.dp.toIntPx(), imageHeight.dp.toIntPx()),
                    dstOffset = center,
                    dstSize = IntSize(imageWidth.dp.toIntPx(), imageHeight.dp.toIntPx())
                )
            }
        })

        return Rect(
            center.toOffset(),
            IntSize(imageWidth.dp.toIntPx(), imageHeight.dp.toIntPx()).toSize()
        )
    }

    fun jump() {
        isGoingUp = true
    }

    fun dying() {
        upVelocity = fallDownVelocity
    }
}
