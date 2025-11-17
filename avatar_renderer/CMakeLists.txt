cmake_minimum_required(VERSION 3.16)
project(AvatarRenderer)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(OpenGL REQUIRED)
find_package(glfw3 REQUIRED)

include_directories(include)

add_library(avatar_renderer SHARED
    src/renderer.cpp
    src/avatar.cpp
    src/rigging.cpp
    src/lip_sync.cpp
    src/emotions.cpp
    src/shaders.cpp
)

target_link_libraries(avatar_renderer OpenGL::GL glfw)

# FFI bridge
add_library(avatar_renderer_ffi SHARED
    src/ffi_bridge.cpp
)
target_link_libraries(avatar_renderer_ffi avatar_renderer)
