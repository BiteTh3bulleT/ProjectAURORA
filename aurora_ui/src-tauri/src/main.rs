#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]

use tauri::{command, State, Window};
use std::sync::Arc;
use tokio::sync::Mutex;

// Shared state for kernel communication
#[derive(Default)]
struct AppState {
    kernel_sender: Option<tokio::sync::mpsc::UnboundedSender<String>>,
}

#[command]
async fn generate_image(
    prompt: String,
    seed: u64,
    cfg: f32,
    state: State<'_, Arc<Mutex<AppState>>>,
) -> Result<String, String> {
    let state = state.lock().await;
    if let Some(sender) = &state.kernel_sender {
        let cmd = format!("generate:{}:{}:{}", prompt, seed, cfg);
        sender.send(cmd).map_err(|e| e.to_string())?;
        Ok("Image generation started".to_string())
    } else {
        Err("Kernel not connected".to_string())
    }
}

#[command]
async fn load_model(
    name: String,
    state: State<'_, Arc<Mutex<AppState>>>,
) -> Result<String, String> {
    let state = state.lock().await;
    if let Some(sender) = &state.kernel_sender {
        let cmd = format!("load_model:{}", name);
        sender.send(cmd).map_err(|e| e.to_string())?;
        Ok("Model loading started".to_string())
    } else {
        Err("Kernel not connected".to_string())
    }
}

#[command]
async fn get_gpu_usage() -> Result<String, String> {
    // Implement GPU usage monitoring
    Ok("GPU usage: 45%".to_string())
}

fn main() {
    let (tx, mut rx) = tokio::sync::mpsc::unbounded_channel::<String>();

    let app_state = Arc::new(Mutex::new(AppState {
        kernel_sender: Some(tx),
    }));

    tauri::Builder::default()
        .manage(app_state)
        .invoke_handler(tauri::generate_handler![
            generate_image,
            load_model,
            get_gpu_usage
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");

    // Kernel communication loop
    tokio::spawn(async move {
        while let Some(msg) = rx.recv().await {
            // Send to kernel
            println!("Received command: {}", msg);
        }
    });
}
