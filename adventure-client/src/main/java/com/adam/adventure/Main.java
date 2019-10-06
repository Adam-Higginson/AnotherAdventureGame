package com.adam.adventure;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.network.NetworkManagerComponent;
import com.adam.adventure.entity.component.tilemap.TilemapComponent;
import com.adam.adventure.entity.component.tilemap.TilemapRendererComponent;
import com.adam.adventure.entity.component.ui.UiManagerComponent;
import com.adam.adventure.entity.component.ui.console.UiConsoleComponentFactory;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.InitialisedEvent;
import com.adam.adventure.loop.GameLoop;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.module.AdventureClientModule;
import com.adam.adventure.render.shader.ProgramFactory;
import com.adam.adventure.render.shader.Shader;
import com.adam.adventure.render.shader.ShaderCompiler;
import com.adam.adventure.render.texture.TextureCache;
import com.adam.adventure.scene.RequestNewSceneEvent;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.window.Window;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class Main {

    private void run() throws Exception {
        MDC.put("frameId", "0");

        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        //Needed to stop AWT running under the main thread which causes issues for GLFW on OSX
        System.setProperty("java.awt.headless", "true");

        configureOpenGlContext();

        final Injector injector = Guice.createInjector(new AdventureClientModule());
        //Initialise texture cache to allow it to start listening to events
        injector.getInstance(TextureCache.class);

        //Show window
        final Window window = injector.getInstance(Window.class);
        window.openWindow();
        window.clearWindow(0.0f, 0.2f, 0.2f, 0.0f);

        compileShaders(injector.getInstance(ShaderCompiler.class), injector.getInstance(ProgramFactory.class));

        addRootEntities(injector.getInstance(EntityFactory.class),
                injector.getInstance(SceneManager.class),
                injector.getInstance(UiConsoleComponentFactory.class));

        addTitleScreenScene(injector.getInstance(SceneManager.class));
        addStartScene(injector.getInstance(SceneManager.class));
        addTestScene(injector.getInstance(SceneManager.class), injector.getInstance(EntityFactory.class));

        //Notify everything that game is ready
        final EventBus eventBus = injector.getInstance(EventBus.class);
        eventBus.publishEvent(new InitialisedEvent());
        eventBus.publishEvent(new RequestNewSceneEvent("TitleScene"));

        loop(window, injector.getInstance(LoopIteration.class));
        //      loop(window, injector.getInstance(DebugLoopIterationImpl.class));


        injector.getInstance(SceneManager.class).shutdown();
        window.close();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void configureOpenGlContext() {
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    }

    private void compileShaders(final ShaderCompiler shaderCompiler, final ProgramFactory programFactory) throws IOException {
        final String vertexShaderSource = readShaderSource("testVert.glsl");
        final Shader vertexShader = shaderCompiler.compileVertexShader(vertexShaderSource);

        final String fragmentShaderSource = readShaderSource("testFrag.glsl");
        final Shader fragmentShader = shaderCompiler.compileFragmentShader(fragmentShaderSource);

        //TODO put set up of programs in a ProgramRegistrar class which reads from config and extract program names to constants
        programFactory.registerProgramFromShaders(vertexShader, fragmentShader, "Test Program");
        compileTilemapShaders(shaderCompiler, programFactory);
    }

    private void compileTilemapShaders(final ShaderCompiler shaderCompiler, final ProgramFactory programFactory) throws IOException {
        final String vertexShaderSource = readShaderSource("assets/shaders/tilemap-vert.glsl");
        final Shader vertexShader = shaderCompiler.compileVertexShader(vertexShaderSource);

        final String fragmentShaderSource = readShaderSource("assets/shaders/tilemap-frag.glsl");
        final Shader fragmentShader = shaderCompiler.compileFragmentShader(fragmentShaderSource);

        programFactory.registerProgramFromShaders(vertexShader, fragmentShader, "TilemapProgram");
    }


    private void addRootEntities(final EntityFactory entityFactory,
                                 final SceneManager sceneManager,
                                 final UiConsoleComponentFactory uiConsoleComponentFactory) {
        final Entity uiManager = entityFactory.create("UI Manager")
                .addComponent(new UiManagerComponent());

        final Entity commandConsole = entityFactory.create("Command Console")
                .addComponent(uiConsoleComponentFactory.buildDefaultUiConsoleComponent());

        final Entity networkEntity = entityFactory.create("Network manager")
                .addComponent(new NetworkManagerComponent());

        sceneManager.addRootEntity(uiManager)
                .addRootEntity(commandConsole)
                .addRootEntity(networkEntity);
    }

    private void addTitleScreenScene(final SceneManager sceneManager) {
        sceneManager.addScene("TitleScene", () -> sceneManager.getSceneFactory().createScene("TitleScene"));
    }


    private void addStartScene(
            final SceneManager sceneManager) {

        sceneManager.addScene("StartScene",
                () -> sceneManager.getSceneFactory().createScene("StartScene"));
    }

    private void addTestScene(final SceneManager sceneManager, final EntityFactory entityFactory) {
        sceneManager.addScene("Test Scene", () -> {
            final Entity tilemapEntity = entityFactory.create("Tilemap")
                    .addComponent(new TilemapComponent("tilemaps/test-world.json"))
                    .addComponent(new TilemapRendererComponent());

            return sceneManager.getSceneFactory()
                    .createScene("Test Scene")
                    .addEntity(tilemapEntity);
        });
    }

    private String readShaderSource(final String resourceLocation) throws IOException {
        final URL resource = Resources.getResource(resourceLocation);
        return Resources.toString(resource, Charsets.UTF_8);
    }


    private void loop(final Window window, final LoopIteration loopIteration) {
        GameLoop.loopUntil(window::shouldClose)
                .andUponEachLoopIterationPerform(loopIteration)
                .loop();
    }

    public static void main(final String[] args) throws Exception {
        new Main().run();
    }
}