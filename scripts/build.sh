#!/bin/bash

# Build AURORA Kernel
echo "Building AURORA Kernel..."
cd aurora_kernel
cargo build --release
cd ..

# Build Avatar Renderer
echo "Building Avatar Renderer..."
cd avatar_renderer
mkdir -p build
cd build
cmake ..
make -j$(nproc)
cd ../..

# Build Tauri UI
echo "Building Tauri UI..."
cd aurora_ui
npm install
npm run build
cd ..

echo "Build complete!"
