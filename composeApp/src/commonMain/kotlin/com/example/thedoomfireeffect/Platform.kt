package com.example.thedoomfireeffect

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform