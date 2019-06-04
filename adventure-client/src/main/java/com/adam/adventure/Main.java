package com.adam.adventure;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.CameraTargetComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.entity.component.SpriteRendererComponent;
import com.adam.adventure.entity.component.console.UiConsoleComponentFactory;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.entity.component.network.NetworkManagerComponent;
import com.adam.adventure.entity.component.network.NetworkTransformComponent;
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
import com.adam.adventure.scene.NewSceneEvent;
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
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class Main {

    private void run() throws Exception {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        //Needed to stop AWT running under the main thread which causes issues for GLFW on OSX
        System.setProperty("java.awt.headless", "true");

        configureOpenGlContext();

        final Injector injector = Guice.createInjector(new AdventureClientModule());

        //Show window
        final Window window = injector.getInstance(Window.class);
        window.openWindow();
        window.clearWindow(0.0f, 0.2f, 0.2f, 0.0f);

        compileShaders(injector.getInstance(ShaderCompiler.class), injector.getInstance(ProgramFactory.class));


        addStartScene(injector.getInstance(EntityFactory.class),
                injector.getInstance(TextureFactory.class),
                injector.getInstance(UiConsoleComponentFactory.class),
                injector.getInstance(SceneManager.class));

        addTestScene(injector.getInstance(EntityFactory.class),
                injector.getInstance(TextureFactory.class),
                injector.getInstance(SceneManager.class));


        //Notify everything that game is ready
        final EventBus eventBus = injector.getInstance(EventBus.class);
        eventBus.publishEvent(new InitialisedEvent());
        eventBus.publishEvent(new NewSceneEvent("StartScene"));

        loop(window, injector.getInstance(LoopIteration.class));
        //       loop(window, injector.getInstance(DebugLoopIterationImpl.class));


        injector.getInstance(SceneManager.class).forceDestroy();
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
    }


    private void addStartScene(final EntityFactory entityFactory,
                               final TextureFactory textureFactory,
                               final UiConsoleComponentFactory uiConsoleComponentFactory,
                               final SceneManager sceneManager) throws IOException {

        final Entity commandConsole = entityFactory.create("Command Console")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(uiConsoleComponentFactory.buildDefaultUiConsoleComponent());

        final Texture playerTexture;
        try (final InputStream playerTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/player-test.png")) {
            playerTexture = textureFactory.loadTextureFromPng(playerTextureInputStream);
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


        //Create components
        final KeyboardMovementComponent keyboardMovementComponent = new KeyboardMovementComponent(.2f);
        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 96f, 96f), 64f, 64f);
        final AnimatedSpriteRendererComponent animatedSpriteRendererComponent = new AnimatedSpriteRendererComponent.Builder(sprite)
                .onEventStopAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH, moveUpAnimation)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST, moveEastAnimation)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST, moveWestAnimation)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH, moveDownAnimation)
                .build();
        final CameraTargetComponent cameraTargetComponent = new CameraTargetComponent();


        //Create player
        final Entity playerEntity = entityFactory.create("Player")
                .addComponent(new NetworkTransformComponent(true))
                .addComponent(keyboardMovementComponent)
                .addComponent(animatedSpriteRendererComponent)
                .addComponent(cameraTargetComponent);


        final Sprite otherPlayerSprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 96f, 96f), 64f, 64f);
        final SpriteAnimation moveUpAnimation1 = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 384.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 384.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 384.f, 96f, 96f))
                .build();

        final SpriteAnimation moveEastAnimation1 = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 480.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 480.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 480.f, 96f, 96f))
                .build();

        final SpriteAnimation moveWestAnimation1 = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 576.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 576.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 576.f, 96f, 96f))
                .build();

        final SpriteAnimation moveDownAnimation1 = new SpriteAnimation.Builder(50, true)
                .addAnimationFrame(new Rectangle(0.0f, 672.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(96f, 672.f, 96f, 96f))
                .addAnimationFrame(new Rectangle(192f, 672.f, 96f, 96f))
                .build();

        final Entity networkEntity = entityFactory.create("Network manager")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(new NetworkManagerComponent(() -> playerEntity, () -> entityFactory.create("Player2")
                        .addComponent(new NetworkTransformComponent(false))
                        .addComponent(new AnimatedSpriteRendererComponent.Builder(otherPlayerSprite)
                                .onEventStopAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT)
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH, moveUpAnimation1)
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST, moveEastAnimation1)
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST, moveWestAnimation1)
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH, moveDownAnimation1)
                                .build())));


        final Scene scene = sceneManager.getSceneFactory().createScene("StartScene")
                .addEntity(commandConsole)
                .addEntity(networkEntity);
        sceneManager.addScene("StartScene", scene);
    }

    private Scene addTestScene(
            final EntityFactory entityFactory,
            final TextureFactory textureFactory,
            final SceneManager sceneManager) throws IOException {

        final Texture tileTexture;
        try (final InputStream tileTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/wood.png")) {
            tileTexture = textureFactory.loadTextureFromPng(tileTextureInputStream);
        }

        final Scene scene = sceneManager.getSceneFactory().createScene("Test Scene");
        sceneManager.addScene("Test Scene", scene);

        //Create random wood tile
        final Sprite woodSprite = new Sprite(tileTexture, new Rectangle(0.0f, 0.0f, 16f, 16f), 64f, 64f);
        final SpriteRendererComponent spriteRendererComponent = new SpriteRendererComponent(woodSprite);

        final Entity tileEntity = entityFactory.create("Wood")
                .addComponent(spriteRendererComponent);
        scene.addEntity(tileEntity);


        //Create new player tile
        final Texture playerMaleTexture;
        try (final InputStream playerMaleTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/PlayerCharacterMale.png")) {
            playerMaleTexture = textureFactory.loadTextureFromPng(playerMaleTextureInputStream);
        }

        final Sprite newPlayerSprite = new Sprite(playerMaleTexture, new Rectangle(0.0f, 64.0f, 32f, 32f), 32f, 32f);
        final SpriteRendererComponent newPlayerSpriteRenderer = new SpriteRendererComponent(newPlayerSprite);

        final Entity newPlayerSpriteEntity = entityFactory.create("NewPlayer")
                .addComponent(newPlayerSpriteRenderer);
        scene.addEntity(newPlayerSpriteEntity);

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