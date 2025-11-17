# Build Instructions

## Prerequisites

- Rust (latest stable)
- C++17/20 compiler (GCC/Clang/MSVC)
- CMake 3.16+
- Node.js 16+
- OpenGL development libraries
- GLFW, GLEW (for C++ rendering)

## Linux Dependencies

```bash
sudo apt-get install build-essential cmake libgl1-mesa-dev libglfw3-dev libglew-dev
```

## macOS Dependencies

```bash
brew install cmake glfw glew
```

## Windows Dependencies

Install Visual Studio with C++ build tools, CMake, and vcpkg for dependencies.

## Building

1. Clone the repository:
   ```bash
   git clone https://github.com/BiteTh3bulleT/ProjectAURORA.git
   cd ProjectAURORA
   ```

2. Run the build script:
   ```bash
   chmod +x scripts/build.sh
   ./scripts/build.sh
   ```

   This will:
   - Build the Rust kernel with Cargo
   - Build the C++ renderer with CMake
   - Build the Tauri UI application

## Running

After building, run:
```bash
./scripts/run.sh
```

This starts the kernel and UI simultaneously.

## Development

For development with hot reloading:
```bash
cd aurora_ui
npm run tauri dev
```

## Troubleshooting

- Ensure all dependencies are installed
- Check that Rust and Cargo are in PATH
- Verify CMake can find OpenGL/GLFW/GLEW
- For GPU acceleration, ensure CUDA/Vulkan SDKs are installed
