#ifndef EMOTIONS_H
#define EMOTIONS_H

#include <string>
#include <vector>

class EmotionMorph {
public:
    EmotionMorph();
    void setEmotion(const std::string& emotion);
    std::vector<float> getMorphWeights();

private:
    std::vector<float> morphWeights;
};

#endif // EMOTIONS_H
