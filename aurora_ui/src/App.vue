<template>
  <div id="app" class="dark">
    <div class="min-h-screen bg-gray-900 text-white">
      <header class="bg-gray-800 p-4">
        <h1 class="text-2xl font-bold">AURORA</h1>
        <nav class="mt-2">
          <button @click="activeTab = 'generate'" :class="activeTab === 'generate' ? 'bg-blue-600' : 'bg-gray-700'" class="px-4 py-2 rounded mr-2">Generate</button>
          <button @click="activeTab = 'history'" :class="activeTab === 'history' ? 'bg-blue-600' : 'bg-gray-700'" class="px-4 py-2 rounded mr-2">History</button>
          <button @click="activeTab = 'settings'" :class="activeTab === 'settings' ? 'bg-blue-600' : 'bg-gray-700'" class="px-4 py-2 rounded">Settings</button>
        </nav>
      </header>

      <main class="p-4">
        <div v-if="activeTab === 'generate'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <h2 class="text-xl mb-4">Image Generation</h2>
            <textarea v-model="prompt" placeholder="Enter your prompt..." class="w-full p-2 bg-gray-800 rounded mb-4" rows="4"></textarea>
            <div class="grid grid-cols-2 gap-4 mb-4">
              <div>
                <label class="block text-sm mb-1">Seed</label>
                <input v-model.number="seed" type="number" class="w-full p-2 bg-gray-800 rounded">
              </div>
              <div>
                <label class="block text-sm mb-1">CFG Scale</label>
                <input v-model.number="cfg" type="number" step="0.1" class="w-full p-2 bg-gray-800 rounded">
              </div>
            </div>
            <button @click="generateImage" class="bg-blue-600 px-4 py-2 rounded w-full">Generate</button>
          </div>
          <div>
            <h2 class="text-xl mb-4">Preview</h2>
            <div class="bg-gray-800 rounded p-4 h-64 flex items-center justify-center">
              <canvas ref="previewCanvas" width="256" height="256" class="border border-gray-700"></canvas>
            </div>
            <div class="mt-4">
              <h3 class="text-lg mb-2">Avatar Viewport</h3>
              <canvas ref="avatarCanvas" width="256" height="256" class="border border-gray-700 bg-gray-800"></canvas>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'history'" class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div v-for="image in history" :key="image.id" class="bg-gray-800 rounded p-4">
            <img :src="image.src" alt="Generated image" class="w-full h-32 object-cover rounded mb-2">
            <p class="text-sm">{{ image.prompt }}</p>
          </div>
        </div>

        <div v-if="activeTab === 'settings'">
          <h2 class="text-xl mb-4">Settings</h2>
          <div class="bg-gray-800 rounded p-4">
            <div class="mb-4">
              <label class="block text-sm mb-1">Model</label>
              <select v-model="selectedModel" class="w-full p-2 bg-gray-700 rounded">
                <option value="sd15">Stable Diffusion 1.5</option>
                <option value="sdxl">Stable Diffusion XL</option>
              </select>
            </div>
            <div class="mb-4">
              <label class="block text-sm mb-1">GPU Usage</label>
              <div class="w-full bg-gray-700 rounded-full h-2.5">
                <div class="bg-blue-600 h-2.5 rounded-full" :style="{ width: gpuUsage + '%' }"></div>
              </div>
              <p class="text-sm mt-1">{{ gpuUsage }}%</p>
            </div>
            <button @click="loadModel" class="bg-blue-600 px-4 py-2 rounded">Load Model</button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { invoke } from '@tauri-apps/api/tauri'

const activeTab = ref('generate')
const prompt = ref('')
const seed = ref(42)
const cfg = ref(7.5)
const selectedModel = ref('sd15')
const gpuUsage = ref(45)
const history = ref([])
const previewCanvas = ref<HTMLCanvasElement>()
const avatarCanvas = ref<HTMLCanvasElement>()

const generateImage = async () => {
  try {
    const result = await invoke('generate_image', { prompt: prompt.value, seed: seed.value, cfg: cfg.value })
    console.log(result)
    // Update preview canvas with generated image
  } catch (error) {
    console.error(error)
  }
}

const loadModel = async () => {
  try {
    const result = await invoke('load_model', { name: selectedModel.value })
    console.log(result)
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  // Initialize canvases and start rendering loops
})
</script>

<style scoped>
/* Additional styles if needed */
</style>
