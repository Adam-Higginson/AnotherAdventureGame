#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform int tilemapWidth;
uniform int tilemapHeight;

void main() {
    mat4 mvp = projection * view * model;

    const vec3 vertices[] = vec3[](
    vec3(-0.5, 0.5, 0.0),
    vec3(0.5, -0.5, 0.0),
    vec3(-0.5, -0.5, 0.0),
    vec3(-0.5, 0.5, 0.0),
    vec3(0.5, 0.5, 0.0),
    vec3(0.5, -0.5, 0.0)
    );

    //TODO 256 isn't right
    const vec2 texcoord[] = vec2[](
    vec2(0.0, 31.0 / 256.0),
    vec2(31.0 / 256.0, 0.0),
    vec2(0.0, 0.0),
    vec2(0.0, 31.0 / 256.0),
    vec2(31.0 / 256.0, 31.0 / 256.0),
    vec2(31.0 / 256.0, 0.0));

    //Here we take the modulus to get rid of remainders, and we use 6 as there are 6 vertices per tile.
    int tileIndex = (gl_VertexID - (gl_VertexID % 6)) / 6;
    int x = tileIndex / tilemapWidth;
    int y = (tileIndex - x) / tilemapHeight;
}