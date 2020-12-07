package com.example.jetpackcomposeflappybird

import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.midX(): Float {
    return ((size.width) / 2)
}

fun DrawScope.midY(): Float {
    return ((size.height) / 2)
}
