#version 330 core

in vec2 normalisedTexture;

out vec4 color;
uniform vec4 someColour; // We set this variable in the OpenGL code.
uniform sampler2D ourTexture;

void main()
{
    color = texture(ourTexture, normalisedTexture);
}