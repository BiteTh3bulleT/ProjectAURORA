#ifndef FFI_BRIDGE_H
#define FFI_BRIDGE_H

#include <cstdint>

extern "C" {
    void* create_renderer();
    void destroy_renderer(void* renderer);
    void init_renderer(void* renderer, int width, int height);
    void render_frame(void* renderer);
    void set_avatar_data(void* renderer, const float* vertices, int vertex_count, const uint32_t* indices, int index_count);
    void update_facial_rigging(void* renderer, const float* blend_shapes, int count);
    void update_lip_sync(void* renderer, const char* phoneme);
    void update_emotions(void* renderer, const char* emotion);
    void apply_hologram_effect(void* renderer, float intensity);
    void get_frame_buffer(void* renderer, uint8_t* buffer, int size);
}

#endif // FFI_BRIDGE_H
