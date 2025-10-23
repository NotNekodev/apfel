#version 330 core

in vec3 vertexColor;
in vec2 texCoord;
out vec4 FragColor;

uniform sampler2D tex0;
uniform bool hasTexture;

void main() {
    if (hasTexture) {
        FragColor = texture(tex0, texCoord) * vec4(vertexColor, 1.0);
    } else {
        FragColor = vec4(vertexColor, 1.0);
    }
}