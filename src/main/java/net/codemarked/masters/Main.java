package net.codemarked.masters;

import lombok.Getter;
import net.codemarked.masters.render.event.EventProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWErrorCallback.createPrint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    public static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static long window;
    public static int windowWidth = 1280, windowHeight = 720;
    @Getter
    private static boolean vsync;

    @Getter
    private static Engine engine;

    public static void main(String[] args) {
        LOGGER.info("LWJGL version: {}", Version.getVersion());
        init();
        loop();
        cleanup();
    }

    public static void init() {
        createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Couldn't initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(windowWidth, windowHeight, "Langton's Ant 3d", 0, 0);
        if (window == NULL) throw new RuntimeException("Couldn't create GLFW window");
        glfwMakeContextCurrent(window);
        setVsync(true);
        glfwShowWindow(window);
        createCapabilities();

        engine = new Engine(window, windowWidth, windowHeight);
        glfwSetFramebufferSizeCallback(window, Main::onWindowResize);
        EventProcessor eventProcessor = engine.getEventProcessor();
        glfwSetCursorPosCallback(window, eventProcessor::onMouseMove);
        glfwSetMouseButtonCallback(window, eventProcessor::onMouseButton);
        glfwSetScrollCallback(window, eventProcessor::onMouseScroll);
        glfwSetKeyCallback(window, eventProcessor::onKey);
        glfwSetCharCallback(window, eventProcessor::onChar);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        engine.init();
    }

    private static void onWindowResize(long window, int width, int height) {
        windowWidth = width;
        windowHeight = height;
        glViewport(0, 0, width, height);
        engine.setViewport(width, height);
    }

    public static void loop() {
        try {
            while (!glfwWindowShouldClose(window)) {
                engine.process();
                engine.update();
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
                engine.render();
                glfwSwapBuffers(window);
                glfwPollEvents();
            }
        } catch (Exception e) {
            LOGGER.error("Game loop error", e);
        }
    }

    public static void setVsync(boolean sync) {
        glfwSwapInterval(sync ? 1 : 0);
        vsync = sync;
    }

    public static void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public static void cleanup() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        engine.cleanup();
    }
}