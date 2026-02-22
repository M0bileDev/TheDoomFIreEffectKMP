package com.example.thedoomfireeffect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun App() {
    DoomCompose()
}

@Composable
fun DoomCompose(state: DoomState = DoomState()) {

    var state by remember { mutableStateOf(state) }

    DoomCanvas(state) { canvasMeasurements ->

        //setup fire view
        val arraySize = canvasMeasurements.widthPixel * canvasMeasurements.heightPixel
        val pixelArray = IntArray(arraySize) { 0 }.apply {
            createFireSource(canvasMeasurements)
        }
        state = state.copy(pixels = pixelArray.toList())
        println("Pixel array: ${pixelArray.size}")
    }
}

@Composable
fun DoomCanvas(state: DoomState, measurements: (CanvasMeasurements) -> Unit) = with(state) {

    var canvasState by remember { mutableStateOf(CanvasMeasurements()) }

    Canvas(modifier = Modifier.fillMaxSize().onSizeChanged { size ->
        canvasState = CanvasMeasurements(
            size.width,
            size.height
        )
        measurements(
            canvasState
        )
    }) {

        if (state.pixels.isNotEmpty()) {
            renderFire(
                pixels,
                canvasState.heightPixel,
                canvasState.widthPixel,
                canvasState.pixelSize
            )
        }

    }
}

private fun DrawScope.renderFire(
    firePixels: List<Int>,
    heightPixels: Int,
    widthPixels: Int,
    pixelSize: Int
) {
    for (column in 0 until widthPixels) {
        for (row in 0 until heightPixels - 1) {
            val currentPixelIndex = column + (widthPixels * row)
            val currentPixel = firePixels[currentPixelIndex]
            val color = fireColors[currentPixel]
            println("RenderFire: column: $column, row: $row color: $color")
            drawRect(
                topLeft = Offset(
                    x = (column * pixelSize).toFloat(),
                    y = (row * pixelSize).toFloat()
                ),
                size = Size(
                    width = pixelSize.toFloat(),
                    height = pixelSize.toFloat()
                ),
                color = color
            )
        }
    }
}

@Preview
@Composable
fun PreviewDoomCompose() {
    DoomCompose(DoomState(pixels = listOf(0)))
}

data class DoomState(
    val pixels: List<Int> = emptyList()
)

fun IntArray.createFireSource(canvas: CanvasMeasurements) {
    val overFlowFireIndex = canvas.widthPixel * canvas.heightPixel

    for(fireColor in 0 until fireColors.lastIndex){
        for (column in 0 until canvas.widthPixel) {
            val pixelIndex = (overFlowFireIndex - canvas.widthPixel-canvas.widthPixel*fireColor) + column
            this[pixelIndex] = fireColors.lastIndex - fireColor
        }
    }

}