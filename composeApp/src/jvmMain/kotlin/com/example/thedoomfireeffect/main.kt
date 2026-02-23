package com.example.thedoomfireeffect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.thedoomfireeffect.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TheDoomFIreEffect",
    ) {
        App()
    }
}