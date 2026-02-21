package com.example.thedoomfireeffect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun App() {

    var state by remember { mutableStateOf(DoomState(offset = 0.25f)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            val newOffset = Random.nextDouble(0.2, 0.4)
            state = state.copy(offset = newOffset.toFloat())

        }
    }

    DoomCompose(state)

}

@Composable
fun DoomCompose(state: DoomState) = with(state) {

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Red,
            topLeft = Offset(x = size.width * offset, y = size.height * offset),
            size = Size(
                width = size.width * (1f - offset * 2f),
                height = size.height * (1f - offset * 2f)
            )
        )
    }
}

@Preview
@Composable
fun PreviewDoomCompose() {
    DoomCompose(DoomState(offset = 0.25f))
}

data class DoomState(
    val offset: Float
)