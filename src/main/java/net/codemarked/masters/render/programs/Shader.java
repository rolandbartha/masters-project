package net.codemarked.masters.render.programs;

import lombok.Getter;
import net.codemarked.masters.util.ShaderUtil;

import static org.lwjgl.opengl.GL20.*;

@Getter
public class Shader {

    private final int id;

    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        int vertexShader = load(vertexShaderPath, GL_VERTEX_SHADER);
        int fragmentShader = load(fragmentShaderPath, GL_FRAGMENT_SHADER);

        id = glCreateProgram();
        glAttachShader(id, vertexShader);
        glAttachShader(id, fragmentShader);
        glLinkProgram(id);
        ShaderUtil.checkErrors(id, "PROGRAM");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private int load(String path, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, ShaderUtil.loadShader(path));
        glCompileShader(shader);
        ShaderUtil.checkErrors(shader, shaderType == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT");
        return shader;
    }

    public void use() {
        glUseProgram(id);
    }

    public void cleanup() {
        glDeleteProgram(id);
    }
}