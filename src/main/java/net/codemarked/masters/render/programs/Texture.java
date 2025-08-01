package net.codemarked.masters.render.programs;

import net.codemarked.masters.util.IOUtil;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {

    private int id;

    public Texture(String texturePath) {
        loadTexture(texturePath);
    }

    private void loadTexture(String path) {
        id = glGenTextures();
        bind();
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            try {
                ByteBuffer buffer = STBImage.stbi_load_from_memory(IOUtil.bufferedResource(path, 8192), width, height, channels, 4);

                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glGenerateMipmap(GL_TEXTURE_2D);

                STBImage.stbi_image_free(buffer);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't load texture cause: " + STBImage.stbi_failure_reason());
            }
        }
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}

