#ifndef RIGGING_H
#define RIGGING_H

#include <vector>

class FacialRigging {
public:
    FacialRigging();
    void setBlendShapes(const std::vector<float>& weights);
    std::vector<float> getDeformedVertices(const std::vector<float>& baseVertices);

private:
    std::vector<std::vector<float>> blendShapes;
};

#endif // RIGGING_H
