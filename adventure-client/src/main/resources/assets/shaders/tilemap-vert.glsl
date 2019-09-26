#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

//The tileset size in pixels
uniform int tilesetWidth;
uniform int tilesetHeight;

//The tilemap size in number of tiles (not pixels!)
uniform int tilemapWidth;
uniform int tilemapHeight;

//The number of columns in the tileset
uniform int tileSetColumns;

//How big a tile is, currently only supports uniform tiles
uniform float tileSize;

//The data from the tilemap stored in an integer texture
uniform isampler2D tilemapData;

//Outputs texture coordinates to look into tileset texture
out vec2 T;

void main() {
    mat4 mvp = projection * view * model;

    vec3 vertices[] = vec3[](
        vec3(0.0, tileSize, 0.0),
        vec3(0.0, 0.0, 0.0),
        vec3(tileSize, 0.0, 0.0),
        vec3(tileSize, 0.0, 0.0),
        vec3(tileSize, tileSize, 0.0),
        vec3(0.0, tileSize, 0.0)
    );

    vec2 texcoord[] = vec2[](
        vec2(0.0, tileSize / tilesetHeight),
        vec2(0.0, 0.0),
        vec2(tileSize / tilesetWidth, 0.0),
        vec2(tileSize / tilesetWidth, 0.0),
        vec2(tileSize / tilesetWidth, tileSize / tilesetHeight),
        vec2(0.0, tileSize / tilesetHeight)
    );


    //Here we take the modulus from the original to get rid of remainders, and we use 6 as there are 6 vertices per tile.
    int vertexIndex = gl_VertexID % 6;
    int tileIndex = (gl_VertexID - vertexIndex) / 6;
    int tileX = tileIndex % tilemapWidth;
    int tileY = (tileIndex - tileX) / tilemapWidth;
    int ti = texture(tilemapData, vec2((float(tileX) + 0.5) / tilemapWidth, (float(tileY) + 0.5) / tilemapHeight)).r;

    //We negate the y position as we want to render top to bottom
    vec3 vertexOffset = vec3(float(tileX) * tileSize, float(tileY) * -tileSize, 0.0);
    vec4 vertexPosition = vec4((vertices[vertexIndex] + vertexOffset), 1.0);
    gl_Position = mvp * vertexPosition;

    //Figuring out texture coordinate
    int s = (ti % tileSetColumns) - 1;
    int t = ((ti - s) / tileSetColumns) + 1;

    float texX = (float(s * tileSize)) / tilesetWidth;
    float texY = 1.0 - ((float(t * tileSize)) / tilesetHeight);
    T = texcoord[vertexIndex] + vec2(texX, texY);
}