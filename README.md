# The Doom Fire Effect 🔥

A Kotlin Multiplatform Compose implementation of the classic Doom fire effect, inspired by and based on [Adam Bennett's Adventures in Compose](https://adambennett.dev/2020/04/adventures-in-compose-the-doom-fire-effect/) and his [original repo](https://github.com/ditn/Doom-Compose/tree/master).

---

## ✨ Features

- 🔥 Classic Doom fire pixel simulation
- 🖥️ **Cross-platform support** — runs on Android, macOS, and Windows via Compose Multiplatform
- 📐 **Responsive canvas** — dynamically recalculates pixel grid on resize using `onSizeChanged`
- 🌬️ **Wind direction support** — fire can drift left, right, or rise straight up
- ⚡ Modern Canvas API — uses `drawRect` with `Offset`, `Size`, and `Color` parameters
- 🧮 Adaptive pixel sizing — maintains a fixed column count (`NUMBER_OF_COLUMNS = 50`) and scales pixel size to fit any screen

---

## 🗺️ Roadmap

- [ ] 🎛️ Speed controller — adjust fire animation framerate
- [ ] 🔄 Rotation support — flip or rotate the fire direction
- [ ] 🎨 Color theme switcher — swap between fire palettes (classic Doom, blue, green, etc.)
- [ ] 🔲 Fire size controller — adjust `NUMBER_OF_COLUMNS` at runtime
- [ ] 🌬️ Live wind direction toggle — switch between `Left`, `Right`, and `None` in real time
- [ ] 💾 Save/export a frame as an image

---

## 🏗️ Architecture

```
model/
├── CanvasMeasurements.kt   # Canvas size, pixelSize, widthPixel, heightPixel extensions
└── WindDirection.kt        # Sealed interface for wind: Left | Right | None

components/
└── DoomCompose.kt          # Main composable, fire logic, rendering
```

### How it works

The fire is represented as a flat `IntArray` where each element is a color index into a `fireColors` palette. Each frame:

1. **Fire source** — the bottom rows are seeded with maximum intensity values.
2. **Propagation** — each pixel looks at the pixel below it, subtracts a small random decay, and writes the result upward (with optional wind-induced horizontal drift).
3. **Rendering** — each pixel is drawn as a filled rectangle scaled to `pixelSize`.

The canvas uses `onSizeChanged` to recompute the pixel grid dimensions on every resize, making it work seamlessly across screen sizes and desktop window resizing.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later (for Android)
- IntelliJ IDEA (for Desktop/JVM targets)
- Kotlin 1.9+
- Compose Multiplatform
- AGP 9.x

### Run on Android

```bash
./gradlew :androidApp:installDebug
```

### Run on Desktop (macOS / Windows)

```bash
./gradlew :desktopApp:run
```

---

## 🙏 Credits

- Original concept and implementation: [Adam Bennett](https://adambennett.dev/2020/04/adventures-in-compose-the-doom-fire-effect/) / [@ditn](https://github.com/ditn/Doom-Compose/tree/master)
- Doom fire algorithm originally described by [Fabien Sanglard](https://fabiensanglard.net/doom_fire_psx/)

---

## 📄 License

This project is licensed under the **Apache License 2.0**
