#version 330 core

out vec4 color;
uniform vec4 someColour; // We set this variable in the OpenGL code.

void main()
{
    color = someColour;
}