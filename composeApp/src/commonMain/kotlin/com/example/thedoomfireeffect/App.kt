package com.example.thedoomfireeffect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import com.example.thedoomfireeffect.model.CanvasMeasurements
import com.example.thedoomfireeffect.model.WindDirection
import com.example.thedoomfireeffect.model.heightPixel
import com.example.thedoomfireeffect.model.pixelSize
import com.example.thedoomfireeffect.model.tallerThanWide
import com.example.thedoomfireeffect.model.widthPixel
import com.example.thedoomfireeffect.theme.fireColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.random.Random

private const val TAG = "DoomCompose"

@Composable
fun App() {
    DoomCompose()
//    DoomCompose1()
}

@Composable
fun DoomCompose(state: DoomState = DoomState()) {

    var state by remember { mutableStateOf(state) }
    val scope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    DoomCanvas(state) { canvasMeasurements ->

        job?.cancel()

        //setup fire view
        val arraySize = canvasMeasurements.widthPixel * canvasMeasurements.heightPixel
        val pixelArray = IntArray(arraySize) { 0 }.apply {
            createFireSource(canvasMeasurements)
        }
        state = state.copy(pixels = pixelArray.toList())

        job = scope.launch {
            while (isActive) {
                withContext(Dispatchers.Default) {
                    delay(16)
                    pixelArray.calculateFirePropagation(canvasMeasurements, WindDirection.None)
                }
                withContext(Dispatchers.Main) {
                    state = state.copy(pixels = pixelArray.toList())
                }
            }
        }
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
            try {
                val currentPixel = firePixels[currentPixelIndex]
                val color = fireColors[currentPixel]
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
            } catch (_: Exception) {

            }

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
//    println("$TAG createFireSource canvas: width:${canvas.width} height:${canvas.height}")
    val overFlowFireIndex = canvas.widthPixel * canvas.heightPixel

    for (fireColor in 0 until fireColors.lastIndex) {
        for (column in 0 until canvas.widthPixel) {
            val pixelIndex =
                (overFlowFireIndex - canvas.widthPixel - canvas.widthPixel * fireColor) + column
            this[pixelIndex] = fireColors.lastIndex - fireColor
        }
    }

//    for (column in 0 until canvas.widthPixel) {
//        val pixelIndex = (overFlowFireIndex - canvas.widthPixel) + column
//        this[pixelIndex] = fireColors.size - 1
//    }
}

private fun IntArray.calculateFirePropagation(
    canvasMeasurements: CanvasMeasurements,
    windDirection: WindDirection
) {
//    println("$TAG calculateFirePropagation canvas: width:${canvasMeasurements.width} height:${canvasMeasurements.height}")
    for (column in 0 until canvasMeasurements.widthPixel) {
        for (row in 1 until canvasMeasurements.heightPixel) {
            val currentPixelIndex = column + (canvasMeasurements.widthPixel * row)
            updateFireIntensityPerPixel(
                currentPixelIndex,
                canvasMeasurements,
                windDirection
            )
        }
    }
}

private fun IntArray.updateFireIntensityPerPixel(
    currentPixelIndex: Int,
    measurements: CanvasMeasurements,
    windDirection: WindDirection
) {
    val bellowPixelIndex = currentPixelIndex + measurements.widthPixel
    if (bellowPixelIndex >= measurements.widthPixel * measurements.heightPixel) return

    val offset = if (measurements.tallerThanWide) 2 else 3
    val decay = floor(Random.nextDouble() * offset).toInt()
    val bellowPixelFireIntensity = this[bellowPixelIndex]
    val newFireIntensity = when {
        bellowPixelFireIntensity - decay >= 0 -> bellowPixelFireIntensity - decay
        else -> 0
    }

    val newPosition = when (windDirection) {
        WindDirection.Right -> if (currentPixelIndex - decay >= 0) currentPixelIndex - decay else currentPixelIndex
        WindDirection.Left -> if (currentPixelIndex + decay >= 0) currentPixelIndex + decay else currentPixelIndex
        WindDirection.None -> currentPixelIndex
    }

    this[newPosition] = newFireIntensity
}