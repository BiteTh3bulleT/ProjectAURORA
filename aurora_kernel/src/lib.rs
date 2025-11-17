pub mod kernel;
pub mod memory;
pub mod model_loader;
pub mod gpu_scheduler;
pub mod shared_memory;
pub mod command_bus;
pub mod endpoints;
pub mod safety;
pub mod plugins;
pub mod image_gen;

use std::sync::Arc;
use tokio::sync::RwLock;

pub struct AuroraKernel {
    pub kernel: Arc<RwLock<kernel::Kernel>>,
    pub memory: Arc<RwLock<memory::MemorySystem>>,
    pub model_loader: Arc<RwLock<model_loader::ModelLoader>>,
    pub gpu_scheduler: Arc<RwLock<gpu_scheduler::GpuScheduler>>,
    pub shared_memory: Arc<RwLock<shared_memory::SharedMemory>>,
    pub command_bus: Arc<RwLock<command_bus::CommandBus>>,
    pub endpoints: Arc<RwLock<endpoints::Endpoints>>,
    pub safety: Arc<RwLock<safety::SafetySandbox>>,
    pub plugins: Arc<RwLock<plugins::PluginSystem>>,
    pub image_gen: Arc<RwLock<image_gen::ImageGeneration>>,
}

impl AuroraKernel {
    pub async fn new() -> Self {
        let kernel = Arc::new(RwLock::new(kernel::Kernel::new().await));
        let memory = Arc::new(RwLock::new(memory::MemorySystem::new().await));
        let model_loader = Arc::new(RwLock::new(model_loader::ModelLoader::new().await));
        let gpu_scheduler = Arc::new(RwLock::new(gpu_scheduler::GpuScheduler::new().await));
        let shared_memory = Arc::new(RwLock::new(shared_memory::SharedMemory::new().await));
        let command_bus = Arc::new(RwLock::new(command_bus::CommandBus::new().await));
        let endpoints = Arc::new(RwLock::new(endpoints::Endpoints::new().await));
        let safety = Arc::new(RwLock::new(safety::SafetySandbox::new().await));
        let plugins = Arc::new(RwLock::new(plugins::PluginSystem::new().await));
        let image_gen = Arc::new(RwLock::new(image_gen::ImageGeneration::new().await));

        Self {
            kernel,
            memory,
            model_loader,
            gpu_scheduler,
            shared_memory,
            command_bus,
            endpoints,
            safety,
            plugins,
            image_gen,
        }
    }

    pub async fn run(&self) -> Result<(), Box<dyn std::error::Error>> {
        // Start all subsystems
        let kernel_handle = tokio::spawn(async move {
            self.kernel.write().await.run().await;
        });
        let memory_handle = tokio::spawn(async move {
            self.memory.write().await.run().await;
        });
        let model_loader_handle = tokio::spawn(async move {
            self.model_loader.write().await.run().await;
        });
        let gpu_scheduler_handle = tokio::spawn(async move {
            self.gpu_scheduler.write().await.run().await;
        });
        let shared_memory_handle = tokio::spawn(async move {
            self.shared_memory.write().await.run().await;
        });
        let command_bus_handle = tokio::spawn(async move {
            self.command_bus.write().await.run().await;
        });
        let endpoints_handle = tokio::spawn(async move {
            self.endpoints.write().await.run().await;
        });
        let safety_handle = tokio::spawn(async move {
            self.safety.write().await.run().await;
        });
        let plugins_handle = tokio::spawn(async move {
            self.plugins.write().await.run().await;
        });
        let image_gen_handle = tokio::spawn(async move {
            self.image_gen.write().await.run().await;
        });

        // Wait for all to complete
        tokio::try_join!(
            kernel_handle,
            memory_handle,
            model_loader_handle,
            gpu_scheduler_handle,
            shared_memory_handle,
            command_bus_handle,
            endpoints_handle,
            safety_handle,
            plugins_handle,
            image_gen_handle
        )?;

        Ok(())
    }
}
