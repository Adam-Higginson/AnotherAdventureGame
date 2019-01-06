package com.adam.adventure;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.CameraTargetComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.entity.component.SpriteRendererComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.InitialisedEvent;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.loop.GameLoop;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.loop.LoopIterationImpl;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.render.shader.ProgramFactory;
import com.adam.adventure.render.shader.Shader;
import com.adam.adventure.render.shader.ShaderCompiler;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.texture.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureFactory;
import com.adam.adventure.render.ui.UiManager;
import com.adam.adventure.render.util.Rectangle;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
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

    private void run() throws Exception {
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
        final Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 1.0f));
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


        //Enable scene handling
        final UiManager uiManager = new UiManager(window, inputManager, renderQueue);
        final SceneManager sceneManager = new SceneManager(eventBus, renderer, inputManager, uiManager);


        //TODO obviously move this
        addTestScene(eventBus, window, renderQueue, camera, renderer, playerTexture, tileTexture, inputManager, sceneManager);

        final UpdateStrategy updateStrategy = new PublishEventUpdateStrategy(eventBus);
        final LoopIteration loopIteration = new LoopIterationImpl(inputManager, updateStrategy, renderer);

        //Notify everything that game is ready
        eventBus.publishEvent(new InitialisedEvent());

        loop(renderer, window, loopIteration);

        window.close();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


    private Scene addTestScene(final EventBus eventBus, final Window window, final RenderQueue renderQueue, final Camera camera, final Renderer renderer, final Texture playerTexture, final Texture tileTexture, final InputManager inputManager, final SceneManager sceneManager) {
        final SpriteAnimation moveUpAnimation = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 0.0f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 0.0f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 0.0f, 96f, 96f))
                .build();

        final SpriteAnimation moveEastAnimation = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 96f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 96f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 96f, 96f, 96f))
                .build();

        final SpriteAnimation moveWestAnimation = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 192f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 192f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 192f, 96f, 96f))
                .build();

        final SpriteAnimation moveDownAnimation = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 288f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 288f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 288f, 96f, 96f))
                .build();


        final Scene scene = sceneManager.getSceneFactory().createScene("Test Scene");
        sceneManager.addScene("Test Scene", scene);

        //Create components
        final KeyboardMovementComponent keyboardMovementComponent = new KeyboardMovementComponent(.2f, inputManager);
        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 96f, 96f), 64f, 64f);
        final AnimatedSpriteRendererComponent animatedSpriteRendererComponent = new AnimatedSpriteRendererComponent.Builder(sprite, renderQueue)
                .onEventStopAnimation(ComponentEvent.ENTITY_NO_MOVEMENT)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_NORTH, moveUpAnimation)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_EAST, moveEastAnimation)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_WEST, moveWestAnimation)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_SOUTH, moveDownAnimation)
                .build();
        final CameraTargetComponent cameraTargetComponent = new CameraTargetComponent(window, camera);

        final Sprite woodSprite = new Sprite(tileTexture, new Rectangle(0.0f, 0.0f, 16f, 16f), 64f, 64f);
        final SpriteRendererComponent spriteRendererComponent = new SpriteRendererComponent(woodSprite, renderQueue);

        //Create player
        final Entity playerEntity = new Entity("Player")
                .addComponent(keyboardMovementComponent)
                .addComponent(animatedSpriteRendererComponent)
                .addComponent(cameraTargetComponent);
        scene.addEntity(playerEntity);

        final Entity tileEntity = new Entity("Wood")
                .addComponent(spriteRendererComponent);
        scene.addEntity(tileEntity);
        return scene;
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


    public static void main(final String[] args) throws Exception {
        new Main().run();
    }
}