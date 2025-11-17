#!/bin/bash

# Run AURORA Kernel in background
echo "Starting AURORA Kernel..."
cd aurora_kernel
cargo run --release &
KERNEL_PID=$!
cd ..

# Run Tauri UI
echo "Starting Tauri UI..."
cd aurora_ui
npm run tauri dev &
UI_PID=$!
cd ..

# Wait for processes
wait $KERNEL_PID $UI_PID
