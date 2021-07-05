package com.example.jetpackcomposeflappybird

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewModelScope

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<GameViewModel>()

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bitmaps = BitmapFactory.decodeResource(resources, R.drawable.flappy)
        viewModel.bird = Bird(viewModel.viewModelScope, bitmaps, 3)
        setContent {
            GameConsole()
        }
    }
}
