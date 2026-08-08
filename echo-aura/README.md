# Echo — Native Android Audio Social Application

Echo is an audio-first social platform rewritten natively in Kotlin using Jetpack Compose and Room Database.

## Features

- **The Frequency (Audio Feed)**: Live voice posts stream with interactive waveform equalizer players, pulse counters, and play/pause controls.
- **The Stage (Live Debates)**: Two voices, one truth. Live audio debate motions with Side A vs Side B stances, audience voting, and challenge creation.
- **The Studio (Audio Capture)**: Audio capture interface with a live `00:00.00` monospace timer, record button, caption input, and instant frequency publishing.
- **Radar & Discovery**: Discover voices, handles, category filters (ALL, TRENDING, RISING, LIVE ROOMS), and search.
- **Voice Profile & Bio**: Authenticated profile `@YOU` with Aura score tracker, 30s Voice Bio recorder, and feed tabs (ECHOES, REVERBS, PULSED, DRAFTS).
- **The Terminal**: System settings command center featuring `[ ON ]` / `[ OFF ]` bracketed text controls for Pings, Privacy, and Audio Quality.
- **Whispers & Notifications**: Encrypted 1-on-1 private voice messaging and real-time activity alerts.

## Architecture

- **UI Framework**: Jetpack Compose with Material 3 Utilitarian brutalist aesthetic.
- **Persistence**: Room Database (`EchoDatabase`, `EchoDao`).
- **Audio Engine**: `AudioEngine` wrapping `MediaRecorder` and `MediaPlayer` with dynamic waveform animation.
- **Language & Target**: Kotlin 2.0, Android SDK 35, AGP 8.8.
