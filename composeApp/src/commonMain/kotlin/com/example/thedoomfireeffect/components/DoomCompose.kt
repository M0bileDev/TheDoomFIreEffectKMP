package com.example.thedoomfireeffect.components

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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Devices.PIXEL_8A
import androidx.compose.ui.tooling.preview.Preview
import com.example.thedoomfireeffect.model.CanvasMeasurements
import com.example.thedoomfireeffect.model.WindDirection
import com.example.thedoomfireeffect.model.heightPixel
import com.example.thedoomfireeffect.model.pixelSize
import com.example.thedoomfireeffect.model.tallerThanWide
import com.example.thedoomfireeffect.model.widthPixel
import com.example.thedoomfireeffect.theme.fireColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.random.Random

private const val FIRE_SPEED_MILLIS = 16L

@Composable
fun DoomCompose(initialState: DoomState = DoomState()) {

    var doomState by remember { mutableStateOf(initialState) }
    var canvasMeasurements by remember { mutableStateOf<CanvasMeasurements?>(null) }

    LaunchedEffect(canvasMeasurements) {
        val measurements = canvasMeasurements ?: return@LaunchedEffect

        val arraySize = measurements.widthPixel * measurements.heightPixel
        val pixelArray = IntArray(arraySize).apply {
            createFireSource(measurements)
        }

        while (isActive) {
            delay(FIRE_SPEED_MILLIS)
            withContext(Dispatchers.Default) {
                pixelArray.calculateFirePropagation(measurements, WindDirection.None)
            }
            doomState = doomState.copy(pixels = pixelArray.toList())
        }
    }

    DoomCanvas(doomState, canvasMeasurements) { measurements ->
        canvasMeasurements = measurements
    }

}

@Composable
fun DoomCanvas(
    state: DoomState,
    canvasMeasurements: CanvasMeasurements?,
    measurements: (CanvasMeasurements) -> Unit
) = with(state) {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                measurements(
                    CanvasMeasurements(
                        size.width,
                        size.height
                    )
                )
            }) {

        if (state.pixels.isNotEmpty() && canvasMeasurements != null) {
            renderFire(
                pixels,
                canvasMeasurements.heightPixel,
                canvasMeasurements.widthPixel,
                canvasMeasurements.pixelSize
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
            var currentPixel = 0
            try {
                currentPixel = firePixels[currentPixelIndex]
            } catch (_: Exception) {
            } finally {
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
            }
        }
    }
}

@Preview(device = PIXEL_8A)
@Composable
fun PreviewDoomCompose() {
    DoomCompose(DoomState(pixels = IntArray(5500) { 0 }.toList()))
}

data class DoomState(
    val pixels: List<Int> = emptyList()
)

fun IntArray.createFireSource(canvas: CanvasMeasurements) {
    val overFlowFireIndex = canvas.widthPixel * canvas.heightPixel

    for (fireColor in 0 until fireColors.lastIndex) {
        for (column in 0 until canvas.widthPixel) {
            val pixelIndex =
                (overFlowFireIndex - canvas.widthPixel - canvas.widthPixel * fireColor) + column
            this[pixelIndex] = fireColors.lastIndex - fireColor
        }
    }
}

private fun IntArray.calculateFirePropagation(
    canvasMeasurements: CanvasMeasurements,
    windDirection: WindDirection
) {
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

    //region height of the fire
    val offset = if (measurements.tallerThanWide) 2 else 3
    val decay = floor(Random.nextDouble() * offset).toInt()
    //endregion

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