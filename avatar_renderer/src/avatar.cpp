#include "avatar.h"
#include <fstream>
#include <iostream>
#include <cstdio>

Avatar::Avatar() {}

void Avatar::loadModel(const std::string& path) {
    // Load OBJ or similar model file
    std::ifstream file(path);
    if (!file.is_open()) {
        std::cerr << "Failed to open avatar model file" << std::endl;
        return;
    }

    std::string line;
    while (std::getline(file, line)) {
        if (line.substr(0, 2) == "v ") {
            // Parse vertex
            float x, y, z;
            sscanf(line.c_str(), "v %f %f %f", &x, &y, &z);
            vertices.push_back(x);
            vertices.push_back(y);
            vertices.push_back(z);
        } else if (line.substr(0, 2) == "f ") {
            // Parse face
            unsigned int a, b, c;
            sscanf(line.c_str(), "f %u %u %u", &a, &b, &c);
            indices.push_back(a - 1);
            indices.push_back(b - 1);
            indices.push_back(c - 1);
        }
    }
}

void Avatar::setPose(const std::vector<float>& pose) {
    // Apply pose transformations
}

std::vector<float> Avatar::getVertices() {
    return vertices;
}

std::vector<unsigned int> Avatar::getIndices() {
    return indices;
}
