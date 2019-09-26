#version 330 core

uniform sampler2D tiles;

in vec2 T;
out vec4 fragcolor;

void main() {
    fragcolor = texture(tiles, T);
}

