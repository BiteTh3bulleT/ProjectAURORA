#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;

uniform float hologramIntensity;
uniform vec3 hologramColor;

void main() {
    vec3 norm = normalize(Normal);
    vec3 viewDir = normalize(-FragPos);
    float fresnel = pow(1.0 - max(dot(viewDir, norm), 0.0), 2.0);

    vec3 hologram = hologramColor * fresnel * hologramIntensity;
    FragColor = vec4(hologram, 0.8);
}
