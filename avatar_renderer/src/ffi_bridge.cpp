#include "ffi_bridge.h"
#include "renderer.h"
#include <cstring>

extern "C" {

void* create_renderer() {
    return new Renderer();
}

void destroy_renderer(void* renderer) {
    delete static_cast<Renderer*>(renderer);
}

void init_renderer(void* renderer, int width, int height) {
    static_cast<Renderer*>(renderer)->init(width, height);
}

void render_frame(void* renderer) {
    static_cast<Renderer*>(renderer)->render();
}

void set_avatar_data(void* renderer, const float* vertices, int vertex_count, const uint32_t* indices, int index_count) {
    std::vector<float> verts(vertices, vertices + vertex_count);
    std::vector<unsigned int> inds(indices, indices + index_count);
    static_cast<Renderer*>(renderer)->setAvatarData(verts, inds);
}

void update_facial_rigging(void* renderer, const float* blend_shapes, int count) {
    std::vector<float> bs(blend_shapes, blend_shapes + count);
    static_cast<Renderer*>(renderer)->updateFacialRigging(bs);
}

void update_lip_sync(void* renderer, const char* phoneme) {
    static_cast<Renderer*>(renderer)->updateLipSync(phoneme);
}

void update_emotions(void* renderer, const char* emotion) {
    static_cast<Renderer*>(renderer)->updateEmotions(emotion);
}

void apply_hologram_effect(void* renderer, float intensity) {
    static_cast<Renderer*>(renderer)->applyHologramEffect(intensity);
}

void get_frame_buffer(void* renderer, uint8_t* buffer, int size) {
    auto fb = static_cast<Renderer*>(renderer)->getFrameBuffer();
    std::memcpy(buffer, fb.data(), std::min(size, (int)fb.size()));
}

}
