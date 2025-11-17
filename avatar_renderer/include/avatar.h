#ifndef AVATAR_H
#define AVATAR_H

#include <vector>
#include <string>

class Avatar {
public:
    Avatar();
    void loadModel(const std::string& path);
    void setPose(const std::vector<float>& pose);
    std::vector<float> getVertices();
    std::vector<unsigned int> getIndices();

private:
    std::vector<float> vertices;
    std::vector<unsigned int> indices;
};

#endif // AVATAR_H
