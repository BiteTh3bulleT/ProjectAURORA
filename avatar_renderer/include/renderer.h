#ifndef RENDERER_H
#define RENDERER_H

#include <GLFW/glfw3.h>
#include <vector>
#include <string>

class Renderer {
public:
    Renderer();
    ~Renderer();
    void init(int width, int height);
    void render();
    void setAvatarData(const std::vector<float>& vertices, const std::vector<unsigned int>& indices);
    void updateFacialRigging(const std::vector<float>& blendShapes);
    void updateLipSync(const std::string& phoneme);
    void updateEmotions(const std::string& emotion);
    void applyHologramEffect(float intensity);
    std::vector<unsigned char> getFrameBuffer();

private:
    GLFWwindow* window;
    unsigned int VAO, VBO, EBO, shaderProgram;
    std::vector<float> avatarVertices;
    std::vector<unsigned int> avatarIndices;
    void setupShaders();
};

#endif // RENDERER_H
