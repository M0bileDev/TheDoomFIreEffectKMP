package com.example.thedoomfireeffect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
}

@Composable
fun DoomCompose() {

    val offsetX = 0.25f
    val offsetY = 0.25f

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Red,
            topLeft = Offset(x = size.width * offsetX, y = size.height * offsetY),
            size = Size(
                width = size.width * (1f - offsetX * 2f),
                height = size.height * (1f - offsetY * 2f)
            )
        )
    }
}

@Preview
@Composable
fun PreviewDoomCompose() {
    DoomCompose()
}