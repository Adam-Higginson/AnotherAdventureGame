#version 330 core

uniform sampler2D tiles;

in vec2 T;
out vec4 fragcolor;

void main() {
    //fragcolor = texture(tiles, T);
//    if (T.x == 65) {
//        fragcolor = vec4(1.0, 0.0, 0.0, 1.0);
//    } else if (T.x == 44) {
//        fragcolor = vec4(0.0, 1.0, 0.0, 1.0);
//    } else if (T.x == 45) {
//        fragcolor = vec4(0.0, 0.0, 1.0, 1.0);
//    } else if (T.x == 0) {
//        fragcolor = vec4(1.0, 1.0, 1.0, 1.0);
//    } else {
//        fragcolor = vec4(1.0, 1.0, 1.0, 1.0);
//    }

//    if (T.x > 1) {
//        fragcolor = vec4(1.0, 0.0, 0.0, 1.0);
//    } else {
//        fragcolor = vec4(1.0, 1.0, 1.0, 1.0);
//    }

    //fragcolor = vec4(T.x, 0.0, 0.0, 1.0);
    //vec4 test = texture(tiles, T);
    //fragcolor = vec4(z.x / 60000, 0.0, 0.0, 1.0);


//    if (T.x == 31 && T.y == 2) {
//        fragcolor = vec4(1.0, 0.0, 0.0, 1.0);
//    } else if (T.x == 3 && T.y == 2){
//        fragcolor = vec4(0.0, 1.0, 0.0, 1.0);
//    } else if (T.x == 2 && T.y == 3){
//        fragcolor = vec4(0.0, 0.0, 1.0, 1.0);
//    } else if (T.x == 3 && T.y == 3) {
//        fragcolor = vec4(0.0, 1.0, 1.0, 1.0);
//    } else {
//        fragcolor = vec4(0.0, 0.0, 0.0, 1.0);
//    }

    fragcolor = texture(tiles, T);
    //fragcolor = vec4(1.0, 1.0, 1.0, 1.0);
//    if (T.x == 2) {
//        fragcolor =
//    }
//    fragcolor.a = 1.0;
}

