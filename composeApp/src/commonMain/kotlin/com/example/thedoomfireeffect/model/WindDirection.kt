package com.example.thedoomfireeffect.model

sealed interface WindDirection {
    data object Right : WindDirection
    data object Left : WindDirection
    data object None : WindDirection
}