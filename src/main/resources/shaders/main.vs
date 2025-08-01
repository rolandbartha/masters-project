#version 330 core

flat out vec3 vColor;
layout (location = 0) in int packedData;

uniform ivec3 offset;
uniform mat4 view;
uniform mat4 projection;

float getBrightness(int faceId) {
    switch (faceId) {
        case 0:
            return 0.5; // East
        case 1:
            return 0.6; // West
        case 2:
            return 0.7; // South
        case 3:
            return 0.85; // North
        case 5:
            return 0.4; // Down
        default:
            return 1.0;
    }
}

const vec3 Colors[] = vec3[](
    vec3(0.0, 0.0, 0.0),   //  0 - Air
    vec3(1.0, 1.0, 0.0),   //  1 - Yellow
    vec3(1.0, 0.0, 0.0),   //  2 - Red
    vec3(0.0, 0.0, 1.0),   //  3 - Blue
    vec3(0.0, 1.0, 0.0),   //  4 - Green
    vec3(1.0, 0.0, 1.0),   //  5 - Magenta
    vec3(0.0, 1.0, 1.0),   //  6 - Cyan
    vec3(1.0, 0.5, 0.0),   //  7 - Orange
    vec3(0.6, 0.0, 1.0),   //  8 - Purple
    vec3(0.2, 0.8, 0.2),   //  9 - Lime
    vec3(1.0, 1.0, 1.0),   //  10 - White
    vec3(0.9, 0.6, 0.0),   //  11 - Gold
    vec3(0.6, 0.4, 0.2),   //  12 - Brown
    vec3(0.6, 0.0, 0.0),   //  13 - DarkRed
    vec3(1.0, 0.75, 0.8)   //  14 - Pink
);

void main() {
    int x = packedData & 0x1F;
    int y = (packedData >> 5) & 0x1F;
    int z = (packedData >> 10) & 0x1F;
    int faceId = (packedData >> 15) & 0x07;
    int blockId = (packedData >> 18) & 0x3FFF;

    // Position
    vec3 vPos = vec3(x + offset.x, y + offset.y, z + offset.z);
    // Lighting
    float brightness = getBrightness(faceId);
    // Color
    vColor = Colors[blockId % Colors.length()] * brightness;

    gl_Position = projection * view * vec4(vPos, 1.0f);
}
