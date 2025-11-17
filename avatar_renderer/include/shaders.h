#ifndef SHADERS_H
#define SHADERS_H

#include <string>

class ShaderManager {
public:
    ShaderManager();
    unsigned int createShaderProgram(const std::string& vertexSource, const std::string& fragmentSource);
    void useShader(unsigned int program);
    void setUniform1f(unsigned int program, const std::string& name, float value);
    void setUniform3f(unsigned int program, const std::string& name, float x, float y, float z);

private:
    unsigned int compileShader(const std::string& source, unsigned int type);
};

#endif // SHADERS_H
