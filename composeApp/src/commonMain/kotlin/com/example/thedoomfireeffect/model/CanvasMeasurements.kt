package com.example.thedoomfireeffect.model

import kotlin.math.ceil

const val NUMBER_OF_COLUMNS = 50

data class CanvasMeasurements(
    val width: Int = 0,
    val height: Int = 0
)

val CanvasMeasurements.tallerThanWide: Boolean
    get() = width < height

val CanvasMeasurements.pixelSize: Int
    get() {
        val longestLength = if (tallerThanWide) width else height
        return ceil(longestLength.toDouble() / NUMBER_OF_COLUMNS).toInt()
    }

val CanvasMeasurements.widthPixel: Int
    get() = when {
        tallerThanWide -> NUMBER_OF_COLUMNS
        else -> ceil(width.toDouble() / pixelSize).toInt()
    }

val CanvasMeasurements.heightPixel: Int
    get() = when {
        !tallerThanWide -> NUMBER_OF_COLUMNS
        else -> ceil(height.toDouble() / pixelSize).toInt()
    }