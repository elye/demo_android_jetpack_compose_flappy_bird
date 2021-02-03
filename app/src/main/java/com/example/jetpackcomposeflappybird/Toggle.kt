package com.example.jetpackcomposeflappybird

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun Toggle(
    modifier: Modifier = Modifier,
    animationStart: MutableState<GameState>,
    onToggle: (Boolean) -> Unit
) {
    val toggleState: MutableState<Boolean> = mutableStateOf(false)
    if (animationStart.value.isFinished()) {
        TextButton(modifier = modifier, onClick = {
            toggleState.value = !animationStart.value.isRunning()
            onToggle(toggleState.value)
        }) {
            Text(
                fontSize = 32.sp,
                text = if (!animationStart.value.isFinished())
                    "\n\n\nClick to Stop"
                else
                    "\n\n\nClick to Start"
            )
        }
    }
}
