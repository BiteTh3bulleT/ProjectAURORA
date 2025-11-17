pub mod command_bus;

use std::sync::Arc;
use tokio::sync::{mpsc, RwLock};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug)]
pub enum Command {
    GenerateImage { prompt: String, seed: u64, cfg: f32 },
    LoadModel { name: String },
    Shutdown,
}

pub struct CommandBus {
    sender: mpsc::UnboundedSender<Command>,
    receiver: mpsc::UnboundedReceiver<Command>,
}

impl CommandBus {
    pub async fn new() -> Self {
        let (sender, receiver) = mpsc::unbounded_channel();
        Self { sender, receiver }
    }

    pub async fn run(&mut self) -> Result<(), Box<dyn std::error::Error>> {
        // Command bus run loop
        while let Some(cmd) = self.receiver.recv().await {
            match cmd {
                Command::GenerateImage { prompt, seed, cfg } => {
                    self.handle_generate_image(prompt, seed, cfg).await?;
                }
                Command::LoadModel { name } => {
                    self.handle_load_model(name).await?;
                }
                Command::Shutdown => break,
            }
        }
        Ok(())
    }

    pub fn send_command(&self, cmd: Command) {
        let _ = self.sender.send(cmd);
    }

    async fn handle_generate_image(&mut self, prompt: String, seed: u64, cfg: f32) -> Result<(), Box<dyn std::error::Error>> {
        // Handle image generation command
        Ok(())
    }

    async fn handle_load_model(&mut self, name: String) -> Result<(), Box<dyn std::error::Error>> {
        // Handle load model command
        Ok(())
    }
}
