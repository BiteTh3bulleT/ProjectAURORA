pub mod latent_diffusion;
pub mod unet;
pub mod clip;
pub mod vae;
pub mod scheduler;
pub mod lora;
pub mod attention;

use std::sync::Arc;
use tokio::sync::RwLock;

pub struct ImageGeneration {
    pub latent_diffusion: Arc<RwLock<latent_diffusion::LatentDiffusion>>,
    pub unet: Arc<RwLock<unet::UNet>>,
    pub clip: Arc<RwLock<clip::CLIP>>,
    pub vae: Arc<RwLock<vae::VAE>>,
    pub scheduler: Arc<RwLock<scheduler::Scheduler>>,
    pub lora: Arc<RwLock<lora::LoRA>>,
    pub attention: Arc<RwLock<attention::Attention>>,
}

impl ImageGeneration {
    pub async fn new() -> Self {
        Self {
            latent_diffusion: Arc::new(RwLock::new(latent_diffusion::LatentDiffusion::new().await)),
            unet: Arc::new(RwLock::new(unet::UNet::new().await)),
            clip: Arc::new(RwLock::new(clip::CLIP::new().await)),
            vae: Arc::new(RwLock::new(vae::VAE::new().await)),
            scheduler: Arc::new(RwLock::new(scheduler::Scheduler::new().await)),
            lora: Arc::new(RwLock::new(lora::LoRA::new().await)),
            attention: Arc::new(RwLock::new(attention::Attention::new().await)),
        }
    }

    pub async fn run(&mut self) -> Result<(), Box<dyn std::error::Error>> {
        // Image generation run loop
        loop {
            // Run latent diffusion
            self.latent_diffusion.write().await.run().await?;
            tokio::time::sleep(tokio::time::Duration::from_millis(100)).await;
        }
    }

    pub async fn generate_image(&mut self, prompt: &str, seed: u64, cfg: f32) -> Result<Vec<u8>, Box<dyn std::error::Error>> {
        // Implement full image generation pipeline
        let text_embeds = self.clip.write().await.encode(prompt).await?;
        let noise = self.scheduler.write().await.sample_noise(seed).await?;
        let latents = self.latent_diffusion.write().await.denoise(noise, text_embeds, cfg).await?;
        let image = self.vae.write().await.decode(latents).await?;
        Ok(image)
    }
}
