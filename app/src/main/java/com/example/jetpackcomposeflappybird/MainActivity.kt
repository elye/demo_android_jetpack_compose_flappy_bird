package com.example.jetpackcomposeflappybird

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

class MainActivity : AppCompatActivity() {

    private val gameState: MutableState<GameState> = mutableStateOf(GameState())
    private val jumpState: MutableState<Boolean> = mutableStateOf(false)
    private val gameScore: MutableState<Int> = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameConsole(
                modifier = Modifier.fillMaxSize().pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (gameState.value.isRunning())
                                jumpState.value = !jumpState.value
                        }
                    }
                    true
                },
                gameState,
                jumpState,
                gameScore
            )
            androidx.compose.foundation.Text(
                modifier = Modifier.fillMaxWidth(),
                fontSize = 48.sp,
                text = gameScore.value.toString(),
                textAlign = TextAlign.Center
            )
            Toggle(modifier = Modifier.fillMaxSize(), gameState) {
                if (it)
                    gameState.value = GameState(GameState.Status.RUNNING)
                else
                    gameState.value = GameState(GameState.Status.STOPPED)
            }
        }
    }
}
