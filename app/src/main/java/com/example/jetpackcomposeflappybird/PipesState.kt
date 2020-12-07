package com.example.jetpackcomposeflappybird

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.DeferredResource
import androidx.compose.ui.unit.*

data class PipesState(
    val upPipe: DeferredResource<ImageBitmap>,
    val downPipe: DeferredResource<ImageBitmap>,
    val gameScore: MutableState<Int>
) {
    private val imageWidth = 26
    private val imageHeight = 160
    private val halfGap = 100
    private val velocity = 6

    private val destWidth = imageWidth * 2
    private val destHeight = imageHeight * 3

    private val pipes: MutableList<Pipe> = emptyList<Pipe>().toMutableList()

    fun init(drawScope: DrawScope) {
        pipes.clear()
        pipes.add(Pipe(drawScope, drawScope.size.width.toInt()))
        pipes.add(Pipe(drawScope, (drawScope.size.width * 1.6).toInt()))
    }

    fun move() {
        pipes.forEach {
            it.move(velocity)
        }
    }

    fun draw(drawScope: DrawScope, timeTick: Float): List<Rect> {
        return drawScope.draw()
    }

    private fun DrawScope.draw(): List<Rect> {
        val rects: MutableList<Rect> = emptyList<Rect>().toMutableList()

        pipes
            .filter { it.currentPosX < - destWidth.dp.toIntPx() }
            .forEach { it.reset() }


        pipes.forEach { pipe ->
            val srcOffset = IntOffset(0, 0)
            val srcSize = IntSize(imageWidth.dp.toIntPx(), imageHeight.dp.toIntPx())
            val dstUpOffSet = IntOffset(pipe.currentPosX, - (pipe.holePos + halfGap).dp.toIntPx())
            val dstDownOffSet = IntOffset(pipe.currentPosX,
                (destHeight - (pipe.holePos - halfGap)).dp.toIntPx())
            val dstSize = IntSize(destWidth.dp.toIntPx(), destHeight.dp.toIntPx())

            upPipe.resource.resource?.let {
                drawImage(
                    image = it,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstOffset = dstUpOffSet,
                    dstSize = dstSize
                )
                rects.add(Rect(dstUpOffSet.toOffset(), dstSize.toSize()))
            }

            downPipe.resource.resource?.let {
                drawImage(
                    image = it,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstOffset = dstDownOffSet,
                    dstSize = dstSize
                )
                rects.add(Rect(dstDownOffSet.toOffset(), dstSize.toSize()))
            }

            if (!pipe.crossHalfWay
                && pipe.currentPosX < (size.width - destWidth.dp.toIntPx()) / 2) {
                pipe.crossHalfWay = true

                gameScore.value++
            }
        }

        return rects
    }

    fun testing() = 1

    data class Pipe(
        val drawScope: DrawScope,
        var currentPosX: Int
    ) {
        var crossHalfWay = false
        var holePos: Int = randomHolePos()

        fun move(velocity: Int) {
            currentPosX -= velocity
        }

        fun reset() {
            currentPosX = drawScope.size.width.toInt()
            holePos = randomHolePos()
            crossHalfWay = false
        }

        private fun randomHolePos() = (100..350).random()
    }
}
