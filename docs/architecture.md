# AURORA Architecture

## Overview

AURORA is a fully working Image Generation AI platform with real-time 3D avatar rendering, built using Rust, C++, and Tauri.

## Components

### AURORA KERNEL (Rust)
- **Core Logic Engine**: Semantic algebra processing, inner/outer loops (Pi-cycle/Phi-cycle)
- **Pyramid Memory System**: Episodic, semantic, and procedural memory layers
- **Model Loader**: ONNX and custom Aurora weights support
- **GPU Scheduler**: CUDA/Vulkan FFI for GPU acceleration
- **Shared Memory Bridge**: FFI to C++ renderer
- **Command Bus**: Tauri UI communication
- **REST/WebSocket Endpoints**: Real-time generation API
- **Safety Sandbox**: Crash isolation and process monitoring
- **Plugin System**: Extensible module architecture

### AVATAR RENDERING MODULE (C++17/20)
- **Real-time 3D Rendering**: Cortana-style avatar visualization
- **Facial Rigging**: Blend shape deformation
- **Lip-sync Engine**: Phoneme-based mouth animation
- **Emotion Morph System**: Dynamic facial expressions
- **Shader-based Hologram Effects**: GPU-accelerated visual effects
- **FFI Bridge**: C API for Rust integration

### TAURI UI SHELL (Rust + TypeScript + HTML/CSS)
- **Image Generation UI**: Prompt input, seed/CFG controls
- **Real-time Preview Panel**: Live image generation feedback
- **Integrated Avatar Viewport**: 3D avatar rendering via shared memory
- **Command Palette**: Kernel instruction interface
- **History View**: Generated image gallery
- **Settings & Model Management**: GPU usage monitor, model switching
- **Dark Mode UI**: Reactive layout with Tailwind CSS

## Communication Layer

- **Kernel ↔ Renderer**: Shared memory + FFI
- **Kernel ↔ UI**: Commands, events, WebSocket
- **Renderer ↔ UI**: Streamed frames to canvas via Rust relay

## Image Generation Backend

- **Latent Diffusion**: Core generation pipeline
- **UNet**: Denoising network
- **CLIP Text Encoder**: Prompt processing
- **VAE Encoder/Decoder**: Latent space conversion
- **Scheduler**: DDIM/Euler sampling
- **LoRA Support**: Fine-tuning adaptation
- **FlashAttention**: Memory-efficient attention mechanism

## Build & Deployment

- **Workspace Structure**: Cargo workspace with kernel and UI crates
- **C++ Build**: CMake-based compilation with OpenGL/Vulkan
- **Cross-platform**: Windows/Linux/macOS support via Tauri
- **Packaging**: NSIS installers and binary bundles
