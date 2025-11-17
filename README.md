# AURORA

A fully working Image Generation AI platform with real-time 3D avatar rendering.

## Architecture

- **AURORA KERNEL (Rust)**: Core logic with semantic algebra, pyramid memory, model loading, GPU scheduling, shared memory, command bus, REST/WebSocket endpoints, safety sandbox, plugin system.
- **AVATAR RENDERING MODULE (C++)**: Real-time 3D avatar rendering with facial rigging, lip-sync, emotion morphs, hologram effects.
- **TAURI UI SHELL**: Full application shell with image generation UI, prompt interface, real-time preview, avatar viewport, command palette, history, settings, GPU monitor.

## Building

1. Install Rust, C++ compiler, CMake, Node.js.
2. Clone the repo.
3. Run `scripts/build.sh` to build all components.
4. Run `scripts/run.sh` to start the application.

## Usage

- Launch the Tauri app.
- Enter prompts in the UI.
- Generate images with real-time avatar feedback.
