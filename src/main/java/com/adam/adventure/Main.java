package com.adam.adventure;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.SpriteEntity;
import com.adam.adventure.entity.component.AnimatedSpriteComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.loop.GameLoop;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.loop.LoopIterationImpl;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.Sprite;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.renderable.TileRenderable;
import com.adam.adventure.render.shader.ProgramFactory;
import com.adam.adventure.render.shader.Shader;
import com.adam.adventure.render.shader.ShaderCompiler;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureFactory;
import com.adam.adventure.render.util.Rectangle;
import com.adam.adventure.update.PublishEventUpdateStrategy;
import com.adam.adventure.update.UpdateStrategy;
import com.adam.adventure.window.Window;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.lwjgl.glfw.GLFW.*;

public class Main {

    private void run() throws IOException {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Set up event bus
        final EventBus eventBus = new EventBus();

        //Create window
        final Window window = buildWindow();

        //Show window
        window.openWindow();
        window.clearWindow(0.0f, 0.2f, 0.2f, 0.0f);


        //Prepare rendering pipeline
        final RenderQueue renderQueue = new RenderQueue();
        final Camera camera = new Camera(5f, new Vector3f(0.0f, 0.0f, 1.0f));
        final Renderer renderer = new Renderer(renderQueue, window, camera);

        //Load textures
        final TextureFactory textureFactory = new TextureFactory();
        final Texture playerTexture;
        final Texture tileTexture;
        try (final InputStream playerTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/player-test.png")) {
            playerTexture = textureFactory.loadTextureFromPng(playerTextureInputStream);
        }
        try (final InputStream tileTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/wood.png")) {
            tileTexture = textureFactory.loadTextureFromPng(tileTextureInputStream);
        }


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

        //Create components
        final EntityFactory entityFactory = new EntityFactory(eventBus);
        final KeyboardMovementComponent keyboardMovementComponent = new KeyboardMovementComponent(.2f, inputManager);
        //TODO add name of "subsets" of animation frames to allow keyboard movement component to signal different animations
        final AnimatedSpriteComponent animatedSpriteComponent = new AnimatedSpriteComponent.Builder(300, true)
                .addAnimationFrame(new Rectangle(0.0f, 0.0f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 0.0f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 0.0f, 96f, 96f))
                .build();


        //Create player
        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 96f, 96f), 64f, 64f);
        final Entity playerEntity = entityFactory.newEntity(() -> new SpriteEntity(sprite))
                .addComponent(keyboardMovementComponent)
                .addComponent(animatedSpriteComponent);
        final SpriteRenderable spriteRenderable = renderer.buildRenderable(() -> new SpriteRenderable(playerEntity, sprite, 1));
        renderQueue.addRenderable(spriteRenderable);

        //Create a test object to render
        final Entity tileEntity = entityFactory.newEntity(Entity::new);
        final TileRenderable tileRenderable = renderer.buildRenderable(() -> new TileRenderable(tileEntity, tileTexture));
        renderQueue.addRenderable(tileRenderable);


        final UpdateStrategy updateStrategy = new PublishEventUpdateStrategy(eventBus);
        final LoopIteration loopIteration = new LoopIterationImpl(inputManager, updateStrategy, renderer);

        loop(renderer, window, loopIteration);

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