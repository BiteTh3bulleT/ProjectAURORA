#ifndef LIP_SYNC_H
#define LIP_SYNC_H

#include <string>
#include <vector>

class LipSync {
public:
    LipSync();
    void setPhoneme(const std::string& phoneme);
    std::vector<float> getLipWeights();

private:
    std::vector<float> lipWeights;
};

#endif // LIP_SYNC_H
