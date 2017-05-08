package com.adam.adventure;

import com.adam.adventure.entity.TileEntity;
import com.adam.adventure.event.Event;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventType;
import com.adam.adventure.event.LoggingEventListener;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.loop.GameLoop;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.loop.LoopIterationImpl;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.renderable.TileRenderable;
import com.adam.adventure.render.shader.ProgramFactory;
import com.adam.adventure.render.shader.Shader;
import com.adam.adventure.render.shader.ShaderCompiler;
import com.adam.adventure.state.GameStateMachine;
import com.adam.adventure.state.LoggingGameStateListener;
import com.adam.adventure.window.Window;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;
import java.net.URL;

import static org.lwjgl.glfw.GLFW.*;

public class Main {

    private void run() throws IOException {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Set up event buses
        final EventBus eventBus = new EventBus();
        LoggingEventListener.registerNewLoggingEventListener(eventBus);
        final GameStateMachine gameStateMachine = GameStateMachine.registerNewGameStateMachine(eventBus)
                .addListener(new LoggingGameStateListener());

        //Create window
        eventBus.publishEvent(new Event(EventType.INITIALISING));
        final Window window = buildWindow();

        //Show window
        window.openWindow();
        window.clearWindow(0.0f, 0.2f, 0.2f, 0.0f);


        //Prepare rendering pipeline
        final RenderQueue renderQueue = new RenderQueue();
        final Camera camera = new Camera(5f, new Vector3f(0.0f, 0.0f, 1.0f));
        final Renderer renderer = new Renderer(renderQueue, window, camera);

        //Set key callback
        final InputManager inputManager = new InputManager(window);

        //Compile shaders
        final ShaderCompiler shaderCompiler = new ShaderCompiler();
        final String vertexShaderSource = readShaderSource("testVert.glsl");
        final Shader vertexShader = shaderCompiler.compileVertexShader(vertexShaderSource);

        final String fragmentShaderSource = readShaderSource("testFrag.glsl");
        final Shader fragmentShader = shaderCompiler.compileFragmentShader(fragmentShaderSource);

        //TODO put set up of programs in a ProgramRegistrar class which reads from config and extract program names to constants
        final ProgramFactory programFactory = new ProgramFactory(renderer);
        programFactory.registerProgramFromShaders(vertexShader, fragmentShader, "Test Program");

        //Create a test object to render
        final TileEntity tileEntity = new TileEntity();
        final TileRenderable tileRenderable = renderer.buildRenderable(() -> new TileRenderable(tileEntity));
        renderQueue.addRenderable(tileRenderable);

        final LoopIteration loopIteration = new LoopIterationImpl(inputManager, camera, renderer);

        eventBus.publishEvent(new Event(EventType.LOADED));
        loop(renderer, window, loopIteration);
        eventBus.publishEvent(new Event(EventType.EXITING));

        window.close();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private String readShaderSource(final String resourceLocation) throws IOException {
        final URL resource = Resources.getResource(resourceLocation);
        return Resources.toString(resource, Charsets.UTF_8);
    }

    private Window buildWindow() {

        return new Window.Builder(800, 600)
                .withTitle("Yet another adventure game")
                .withIsVisible(true)
                .withIsResizable(true)
                .build();
    }

    private void loop(final Renderer renderer, final Window window, final LoopIteration loopIteration) {
        GameLoop.loopUntil(window::shouldClose)
                .andUponEachLoopIterationPerform(loopIteration)
                .loop();
    }


    public static void main(final String[] args) throws IOException {
        new Main().run();
    }
}