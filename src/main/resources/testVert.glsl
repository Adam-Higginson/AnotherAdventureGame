#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec2 textureDimensions;

out vec2 normalisedTexture;

void main()
{
    gl_Position = projection * view * model * vec4(position.x, position.y, position.z, 1.0);
    normalisedTexture = vec2(texCoord.x / textureDimensions.x, texCoord.y / textureDimensions.y);
}