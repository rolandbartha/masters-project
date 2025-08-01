package net.codemarked.masters.util;

import net.codemarked.masters.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil {

    public static String loadShader(String path) {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("Couldn't find shader at the provided path: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read shader file: " + path, e);
        }
    }

    public static void checkErrors(int shader, String type) {
        if (type.equals("PROGRAM")) {
            if (glGetProgrami(shader, GL_LINK_STATUS) == GL_FALSE) {
                Main.LOGGER.info("ERROR: Shader program linking failed");
                Main.LOGGER.info(glGetProgramInfoLog(shader));
            }
        } else {
            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                Main.LOGGER.info("ERROR: {} shader compilation failed", type);
                Main.LOGGER.info(glGetShaderInfoLog(shader));
            }
        }
    }
}
