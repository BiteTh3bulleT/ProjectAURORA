#!/bin/bash

echo "Building AURORA..."

# Install system dependencies
echo "Installing system dependencies..."
if command -v apt-get &> /dev/null; then
    # Ubuntu/Debian
    sudo apt-get update
    sudo apt-get install -y build-essential cmake libgl1-mesa-dev libglfw3-dev libglew-dev libx11-dev libxrandr-dev libxinerama-dev libxcursor-dev libxi-dev python3 python3-pip python3-venv
elif command -v zypper &> /dev/null; then
    # OpenSUSE
    sudo zypper install -y cmake glfw-devel glew-devel python3 python3-pip
elif command -v dnf &> /dev/null; then
    # Fedora/RHEL
    sudo dnf install -y cmake glfw-devel glew-devel python3 python3-pip
elif command -v pacman &> /dev/null; then
    # Arch Linux
    sudo pacman -S --noconfirm cmake glfw glew python python-pip
elif command -v brew &> /dev/null; then
    # macOS
    brew install cmake glfw glew python3
else
    echo "Unsupported package manager. Please install dependencies manually:"
    echo "- cmake"
    echo "- glfw3/glew development libraries"
    echo "- python3 and pip"
    exit 1
fi

# Setup Python virtual environment and install PyTorch
echo "Setting up Python environment..."
python3 -m venv venv
source venv/bin/activate
# Install compatible PyTorch version for tch 0.15.0
pip install torch==2.4.1 torchvision==0.19.1 torchaudio==2.4.1 --index-url https://download.pytorch.org/whl/cpu

# Set environment variables for PyTorch
export LIBTORCH_USE_PYTORCH=1

echo "Building AURORA Kernel..."
cd aurora_kernel
cargo build --release --features tch
if [ $? -ne 0 ]; then
    echo "Failed to build AURORA Kernel"
    exit 1
fi
cd ..

echo "Building Avatar Renderer..."
cd avatar_renderer
mkdir -p build
cd build
cmake ..
make -j$(nproc)
if [ $? -ne 0 ]; then
    echo "Failed to build Avatar Renderer"
    exit 1
fi
cd ../..

echo "Building Tauri UI..."
cd aurora_ui
npm install
npm run tauri build
if [ $? -ne 0 ]; then
    echo "Failed to build Tauri UI"
    exit 1
fi
cd ..

echo "Build complete!"
echo "Run './scripts/run.sh' to start AURORA"
