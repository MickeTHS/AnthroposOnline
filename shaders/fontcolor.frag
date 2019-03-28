uniform sampler2D my_color_texture;
uniform vec4 my_color;

void main() {
    //gl_FragColor = texture2D(my_color_texture, gl_TexCoord[0].xy) + vec4(1.0, 0.0, 0.0, 0.0);
    gl_FragColor = texture2D(my_color_texture, gl_TexCoord[0].xy) + my_color;
}