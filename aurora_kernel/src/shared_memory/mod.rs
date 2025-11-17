pub mod shared_memory;

use std::ffi::c_void;
use libc::{shm_open, ftruncate, mmap, PROT_READ, PROT_WRITE, MAP_SHARED};

pub struct SharedMemory {
    ptr: *mut c_void,
    size: usize,
}

impl SharedMemory {
    pub async fn new() -> Self {
        let size = 1024 * 1024; // 1MB
        let fd = unsafe { shm_open(b"aurora_shm\0".as_ptr() as *const i8, libc::O_CREAT | libc::O_RDWR, 0o666) };
        unsafe { ftruncate(fd, size as i64) };
        let ptr = unsafe { mmap(std::ptr::null_mut(), size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0) };
        Self { ptr, size }
    }

    pub async fn run(&mut self) -> Result<(), Box<dyn std::error::Error>> {
        // Shared memory bridge run loop
        loop {
            // Bridge to C++ renderer
            self.bridge_to_renderer().await?;
            tokio::time::sleep(tokio::time::Duration::from_millis(20)).await;
        }
    }

    pub async fn bridge_to_renderer(&mut self) -> Result<(), Box<dyn std::error::Error>> {
        // Implement shared memory bridge to C++
        Ok(())
    }

    pub fn write_data(&mut self, data: &[u8]) {
        unsafe { std::ptr::copy_nonoverlapping(data.as_ptr(), self.ptr as *mut u8, data.len().min(self.size)) };
    }

    pub fn read_data(&self, len: usize) -> Vec<u8> {
        let mut buf = vec![0u8; len.min(self.size)];
        unsafe { std::ptr::copy_nonoverlapping(self.ptr as *const u8, buf.as_mut_ptr(), buf.len()) };
        buf
    }
}

impl Drop for SharedMemory {
    fn drop(&mut self) {
        unsafe { libc::munmap(self.ptr, self.size) };
    }
}
