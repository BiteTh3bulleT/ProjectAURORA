#include "renderer.h"
#include <GL/glew.h>
#include <iostream>

Renderer::Renderer() : window(nullptr), VAO(0), VBO(0), EBO(0), shaderProgram(0) {}

Renderer::~Renderer() {
    if (window) glfwDestroyWindow(window);
    glfwTerminate();
}

void Renderer::init(int width, int height) {
    if (!glfwInit()) {
        std::cerr << "Failed to initialize GLFW" << std::endl;
        return;
    }

    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Offscreen rendering
    window = glfwCreateWindow(width, height, "Avatar Renderer", nullptr, nullptr);
    if (!window) {
        std::cerr << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        return;
    }

    glfwMakeContextCurrent(window);
    glewExperimental = GL_TRUE;
    if (glewInit() != GLEW_OK) {
        std::cerr << "Failed to initialize GLEW" << std::endl;
        return;
    }

    setupShaders();
    glGenVertexArrays(1, &VAO);
    glGenBuffers(1, &VBO);
    glGenBuffers(1, &EBO);
}

void Renderer::render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(shaderProgram);
    glBindVertexArray(VAO);
    glDrawElements(GL_TRIANGLES, avatarIndices.size(), GL_UNSIGNED_INT, 0);
    glfwSwapBuffers(window);
}

void Renderer::setAvatarData(const std::vector<float>& vertices, const std::vector<unsigned int>& indices) {
    avatarVertices = vertices;
    avatarIndices = indices;

    glBindVertexArray(VAO);
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, vertices.size() * sizeof(float), vertices.data(), GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size() * sizeof(unsigned int), indices.data(), GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);
}

void Renderer::updateFacialRigging(const std::vector<float>& blendShapes) {
    // Apply blend shapes to vertices
}

void Renderer::updateLipSync(const std::string& phoneme) {
    // Update lip positions based on phoneme
}

void Renderer::updateEmotions(const std::string& emotion) {
    // Apply emotion morphs
}

void Renderer::applyHologramEffect(float intensity) {
    // Apply hologram shader effect
}

std::vector<unsigned char> Renderer::getFrameBuffer() {
    int width, height;
    glfwGetFramebufferSize(window, &width, &height);
    std::vector<unsigned char> buffer(width * height * 3);
    glReadPixels(0, 0, width, height, GL_RGB, GL_UNSIGNED_BYTE, buffer.data());
    return buffer;
}

void Renderer::setupShaders() {
    const char* vertexShaderSource = R"(
        #version 330 core
        layout (location = 0) in vec3 aPos;
        void main() {
            gl_Position = vec4(aPos, 1.0);
        }
    )";

    const char* fragmentShaderSource = R"(
        #version 330 core
        out vec4 FragColor;
        void main() {
            FragColor = vec4(1.0, 0.5, 0.2, 1.0);
        }
    )";

    unsigned int vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, 1, &vertexShaderSource, nullptr);
    glCompileShader(vertexShader);

    unsigned int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, 1, &fragmentShaderSource, nullptr);
    glCompileShader(fragmentShader);

    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertexShader);
    glAttachShader(shaderProgram, fragmentShader);
    glLinkProgram(shaderProgram);

    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
}
