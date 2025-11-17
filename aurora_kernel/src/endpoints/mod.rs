pub mod endpoints;

use axum::{
    extract::ws::{Message, WebSocket, WebSocketUpgrade},
    extract::State,
    response::IntoResponse,
    routing::get,
    Router,
};
use std::net::SocketAddr;
use tokio::sync::mpsc;
use tower_http::cors::CorsLayer;
use serde::{Deserialize, Serialize};

#[derive(Clone)]
pub struct AppState {
    pub command_sender: mpsc::UnboundedSender<super::command_bus::Command>,
}

pub struct Endpoints {
    app: Router,
}

impl Endpoints {
    pub async fn new() -> Self {
        let (command_sender, _) = mpsc::unbounded_channel();
        let state = AppState { command_sender };

        let app = Router::new()
            .route("/generate", get(generate_image))
            .route("/ws", get(websocket_handler))
            .layer(CorsLayer::permissive())
            .with_state(state);

        Self { app }
    }

    pub async fn run(&mut self) -> Result<(), Box<dyn std::error::Error>> {
        let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
        println!("Listening on {}", addr);
        axum::Server::bind(&addr)
            .serve(self.app.clone().into_make_service())
            .await?;
        Ok(())
    }
}

async fn generate_image(
    State(state): State<AppState>,
    axum::extract::Query(params): axum::extract::Query<GenerateParams>,
) -> impl IntoResponse {
    let cmd = super::command_bus::Command::GenerateImage {
        prompt: params.prompt,
        seed: params.seed.unwrap_or(42),
        cfg: params.cfg.unwrap_or(7.5),
    };
    state.command_sender.send(cmd).unwrap();
    "Image generation started"
}

async fn websocket_handler(
    ws: WebSocketUpgrade,
    State(state): State<AppState>,
) -> impl IntoResponse {
    ws.on_upgrade(|socket| handle_socket(socket, state))
}

async fn handle_socket(mut socket: WebSocket, state: AppState) {
    while let Some(msg) = socket.recv().await {
        if let Ok(msg) = msg {
            match msg {
                Message::Text(text) => {
                    if let Ok(cmd) = serde_json::from_str::<super::command_bus::Command>(&text) {
                        state.command_sender.send(cmd).unwrap();
                    }
                }
                Message::Close(_) => break,
                _ => {}
            }
        }
    }
}

#[derive(Deserialize)]
struct GenerateParams {
    prompt: String,
    seed: Option<u64>,
    cfg: Option<f32>,
}
