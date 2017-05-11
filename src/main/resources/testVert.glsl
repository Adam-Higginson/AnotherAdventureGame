#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec2 textureDimensions;
uniform vec2 textureOffset;

out vec2 normalisedTexture;

void main()
{
    gl_Position = projection * view * model * vec4(position.x, position.y, position.z, 1.0);
    normalisedTexture = vec2((texCoord.x + textureOffset.x) / textureDimensions.x, (texCoord.y + textureOffset.y) / textureDimensions.y);
}