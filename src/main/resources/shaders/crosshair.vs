#version 330 core

layout (location = 0) in vec2 vPos;
layout (location = 1) in vec2 vTexCoords;

uniform mat4 projection;

out vec2 vTexCoord;

void main()
{
	gl_Position = projection * vec4(vPos, 0.0, 1.0);
	vTexCoord = vTexCoords;
}