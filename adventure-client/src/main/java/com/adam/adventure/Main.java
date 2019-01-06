package com.adam.adventure;

import com.adam.adventure.client.NetworkClient;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.CameraTargetComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.entity.component.SpriteRendererComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.InitialisedEvent;
import com.adam.adventure.loop.GameLoop;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.module.AdventureClientModule;
import com.adam.adventure.render.shader.ProgramFactory;
import com.adam.adventure.render.shader.Shader;
import com.adam.adventure.render.shader.ShaderCompiler;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.texture.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureFactory;
import com.adam.adventure.render.util.Rectangle;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.window.Window;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
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

        final Injector injector = Guice.createInjector(new AdventureClientModule());

        //Show window
        final Window window = injector.getInstance(Window.class);
        window.openWindow();
        window.clearWindow(0.0f, 0.2f, 0.2f, 0.0f);

        final NetworkClient networkClient = injector.getInstance(NetworkClient.class);
        networkClient.start();

        compileShaders(injector.getInstance(ShaderCompiler.class), injector.getInstance(ProgramFactory.class));
        addTestScene(injector.getInstance(EntityFactory.class),
                injector.getInstance(TextureFactory.class),
                injector.getInstance(SceneManager.class));


        //Notify everything that game is ready
        final EventBus eventBus = injector.getInstance(EventBus.class);
        eventBus.publishEvent(new InitialisedEvent());

        loop(window, injector.getInstance(LoopIteration.class));

        networkClient.stop();
        window.close();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void compileShaders(final ShaderCompiler shaderCompiler, final ProgramFactory programFactory) throws IOException {
        final String vertexShaderSource = readShaderSource("testVert.glsl");
        final Shader vertexShader = shaderCompiler.compileVertexShader(vertexShaderSource);

        final String fragmentShaderSource = readShaderSource("testFrag.glsl");
        final Shader fragmentShader = shaderCompiler.compileFragmentShader(fragmentShaderSource);

        //TODO put set up of programs in a ProgramRegistrar class which reads from config and extract program names to constants
        programFactory.registerProgramFromShaders(vertexShader, fragmentShader, "Test Program");
    }


    private Scene addTestScene(
            final EntityFactory entityFactory,
            final TextureFactory textureFactory,
            final SceneManager sceneManager) throws IOException {

        final Texture playerTexture;
        final Texture tileTexture;
        try (final InputStream playerTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/player-test.png")) {
            playerTexture = textureFactory.loadTextureFromPng(playerTextureInputStream);
        }
        try (final InputStream tileTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/wood.png")) {
            tileTexture = textureFactory.loadTextureFromPng(tileTextureInputStream);
        }


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
        final KeyboardMovementComponent keyboardMovementComponent = new KeyboardMovementComponent(.2f);
        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 96f, 96f), 64f, 64f);
        final AnimatedSpriteRendererComponent animatedSpriteRendererComponent = new AnimatedSpriteRendererComponent.Builder(sprite)
                .onEventStopAnimation(ComponentEvent.ENTITY_NO_MOVEMENT)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_NORTH, moveUpAnimation)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_EAST, moveEastAnimation)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_WEST, moveWestAnimation)
                .onEventSetAnimation(ComponentEvent.ENTITY_MOVE_SOUTH, moveDownAnimation)
                .build();
        final CameraTargetComponent cameraTargetComponent = new CameraTargetComponent();

        final Sprite woodSprite = new Sprite(tileTexture, new Rectangle(0.0f, 0.0f, 16f, 16f), 64f, 64f);
        final SpriteRendererComponent spriteRendererComponent = new SpriteRendererComponent(woodSprite);

        //Create player
        final Entity playerEntity = entityFactory.create("Player")
                .addComponent(keyboardMovementComponent)
                .addComponent(animatedSpriteRendererComponent)
                .addComponent(cameraTargetComponent);
        scene.addEntity(playerEntity);

        final Entity tileEntity = entityFactory.create("Wood")
                .addComponent(spriteRendererComponent);
        scene.addEntity(tileEntity);
        return scene;
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