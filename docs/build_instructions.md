# AURORA Build Instructions

## Prerequisites

AURORA requires the following system dependencies:

### Linux (Ubuntu/Debian)
```bash
sudo apt-get update
sudo apt-get install -y build-essential cmake libgl1-mesa-dev libglfw3-dev libglew-dev libx11-dev libxrandr-dev libxinerama-dev libxcursor-dev libxi-dev python3 python3-pip python3-venv
```

### Linux (OpenSUSE)
```bash
sudo zypper install -y cmake glfw-devel glew-devel python3 python3-pip
```

### Linux (Fedora/RHEL)
```bash
sudo dnf install -y cmake glfw-devel glew-devel python3 python3-pip
```

### Linux (Arch)
```bash
sudo pacman -S --noconfirm cmake glfw glew python python-pip
```

### macOS
```bash
brew install cmake glfw glew python3
```

## Building

### Automatic Build (Recommended)
Run the automated build script which will install all dependencies and build all components:

```bash
./scripts/build.sh
```

### Manual Build

1. **Setup Python Environment**
   ```bash
   python3 -m venv venv
   source venv/bin/activate
   pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu
   export LIBTORCH_USE_PYTORCH=1
   ```

2. **Build AURORA Kernel**
   ```bash
   cd aurora_kernel
   cargo build --release --features tch
   cd ..
   ```

3. **Build Avatar Renderer**
   ```bash
   cd avatar_renderer
   mkdir -p build
   cd build
   cmake ..
   make -j$(nproc)
   cd ../..
   ```

4. **Build Tauri UI**
   ```bash
   cd aurora_ui
   npm install
   npm run tauri build
   cd ..
   ```

## Running

After successful build:

```bash
./scripts/run.sh
```

This will start the AURORA application with all components running.

## Troubleshooting

### PyTorch Issues
- Ensure Python virtual environment is activated
- Check that `LIBTORCH_USE_PYTORCH=1` is set
- Verify PyTorch installation: `python -c "import torch; print(torch.__version__)"`

### OpenGL/GLFW Issues
- Ensure graphics drivers are installed
- Check OpenGL version: `glxinfo | grep "OpenGL version"`

### Build Failures
- Clean build: `rm -rf target/ aurora_renderer/build/ aurora_ui/src-tauri/target/`
- Re-run build script

## Architecture

AURORA consists of three main components:

1. **AURORA Kernel (Rust)**: Core AI engine with image generation
2. **Avatar Renderer (C++)**: Real-time 3D avatar rendering
3. **Tauri UI (Rust + TypeScript)**: Desktop application interface

All components communicate via shared memory and FFI bridges.
