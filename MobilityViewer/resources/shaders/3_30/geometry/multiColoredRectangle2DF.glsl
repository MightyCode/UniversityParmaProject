/*#version 330 core

in vec4 vertexColor; // Input color from vertex shader
out vec4 FragColor;

void main() {
    FragColor = vec4(1, 1, 1, 1); //vertexColor;
}*/

#version 330 core
out vec4 FragColor;

in vec3 fColor;

void main()
{
    FragColor = vec4(fColor, 1.0);
}