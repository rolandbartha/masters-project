package net.codemarked.masters.render;

import net.codemarked.masters.render.programs.Shader;
import net.codemarked.masters.render.programs.Texture;
import net.codemarked.masters.util.math.Mat4F;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.*;

public class Crosshair {
    private final int vao, vbo;
    private static final float SIZE = 13.5f;
    private final Shader shader;
    private final Texture texture;
    private float[] ortho;

    public Crosshair(float width, float height) {
        ortho = Mat4F.of().ortho(0.0f, width, height, 0.0f, 0.0f, 10.0f).getM();
        texture = new Texture("textures/crosshair.png");
        shader = new Shader("shaders/crosshair.vs", "shaders/crosshair.fs");
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        updateAspectRatio(width, height);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    public void updateAspectRatio(float width, float height) {
        ortho = Mat4F.of().ortho(0.0f, width, height, 0.0f, 0.0f, 10.0f).getM();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, new float[]{
                width / 2f - SIZE, height / 2f - SIZE, 0.0f, 0.0f,
                width / 2f + SIZE, height / 2f - SIZE, 1.0f, 0.0f,
                width / 2f + SIZE, height / 2f + SIZE, 1.0f, 1.0f,

                width / 2f - SIZE, height / 2f - SIZE, 0.0f, 0.0f,
                width / 2f - SIZE, height / 2f + SIZE, 0.0f, 1.0f,
                width / 2f + SIZE, height / 2f + SIZE, 1.0f, 1.0f,
        }, GL_STATIC_DRAW);
    }

    public void render() {
        shader.use();
        texture.bind();
        glUniformMatrix4fv(glGetUniformLocation(shader.getId(), "projection"), false, ortho);
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        texture.cleanup();
        shader.cleanup();
    }
}
