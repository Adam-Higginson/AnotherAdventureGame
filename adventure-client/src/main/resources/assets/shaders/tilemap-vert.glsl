#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

//uniform int tilesetWidth;
//uniform int tilesetHeight;

uniform int tilemapWidth;
uniform int tilemapHeight;

uniform isampler2D tilemapData;

out vec2 T;

void main() {
    mat4 mvp = projection * view * model;

    float tilesetWidth = 672.0;
    float tilesetHeight = 701.0;

    const vec3 vertices[] = vec3[](
        vec3(0.0, 32.0, 0.0),
        vec3(0.0, 0.0, 0.0),
        vec3(32.0, 0.0, 0.0),
        vec3(32.0, 0.0, 0.0),
        vec3(32.0, 32.0, 0.0),
        vec3(0.0, 32.0, 0.0)
    );

    vec2 texcoord[] = vec2[](
        vec2(0.0, 32.0 / tilesetHeight),
        vec2(0.0, 0.0),
        vec2(32.0 / tilesetWidth, 0.0),
        vec2(32.0 / tilesetWidth, 0.0),
        vec2(32.0 / tilesetWidth, 32.0 / tilesetHeight),
        vec2(0.0, 32.0 / tilesetHeight)
    );


    //Here we take the modulus from the original to get rid of remainders, and we use 6 as there are 6 vertices per tile.
    int vertexIndex = gl_VertexID % 6;
    int tileIndex = (gl_VertexID - vertexIndex) / 6;
    int tileX = tileIndex % tilemapWidth;
    int tileY = (tileIndex - tileX) / tilemapWidth;
    int ti = texture(tilemapData, vec2((float(tileX) + 0.5) / tilemapWidth, (float(tileY) + 0.5) / tilemapHeight)).r;

    vec3 vertexOffset = vec3(float(tileX) * 32.0, float(tileY) * -32.0, 0.0);
    vec4 vertexPosition = vec4((vertices[vertexIndex] + vertexOffset), 1.0);
    gl_Position = mvp * vertexPosition;

    //Figuring out texture coordinate
    int s = (ti % 21) - 1;
    int t = ((ti - s) / 21) + 1;

    float texX = (float(s * 32)) / tilesetWidth;
    float texY = 1.0 - ((float(t * 32)) / tilesetHeight);
    T = texcoord[vertexIndex] + vec2(texX, texY);
}