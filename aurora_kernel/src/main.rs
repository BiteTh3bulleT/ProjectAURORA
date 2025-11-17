use aurora_kernel::AuroraKernel;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let kernel = AuroraKernel::new().await;
    kernel.run().await?;
    Ok(())
}
