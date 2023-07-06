/*#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec4 aColor; // Vertex attribute for color

uniform mat4 projection;
uniform mat4 view;

out VS_OUT {
    vec4 color;
} vs_out;

void main() {
    gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
    vs_out.color = aColor;
}*/

#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec3 aColor;

out VS_OUT {
    vec3 color;
} vs_out;

void main()
{
    vs_out.color = aColor;
    gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
}