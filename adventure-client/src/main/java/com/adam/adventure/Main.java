package com.adam.adventure;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.CameraTargetComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.entity.component.SpriteRendererComponent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.entity.component.network.NetworkManagerComponent;
import com.adam.adventure.entity.component.network.NetworkTransformComponent;
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
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.sprite.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureCache;
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
        //Initialise texture cache to allow it to start listening to events
        injector.getInstance(TextureCache.class);

        //Show window
        final Window window = injector.getInstance(Window.class);
        window.openWindow();
        window.clearWindow(0.0f, 0.2f, 0.2f, 0.0f);

        compileShaders(injector.getInstance(ShaderCompiler.class), injector.getInstance(ProgramFactory.class));

        addTitleScreenScene(injector.getInstance(SceneManager.class),
                injector.getInstance(EntityFactory.class),
                injector.getInstance(UiConsoleComponentFactory.class));


        addStartScene(injector.getInstance(EntityFactory.class),
                injector.getInstance(TextureFactory.class),
                injector.getInstance(SceneManager.class));

        addTestScene(injector.getInstance(EntityFactory.class),
                injector.getInstance(TextureFactory.class),
                injector.getInstance(SceneManager.class));


        //Notify everything that game is ready
        final EventBus eventBus = injector.getInstance(EventBus.class);
        eventBus.publishEvent(new InitialisedEvent());
        eventBus.publishEvent(new NewSceneEvent("TitleScene"));

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


    private void addTitleScreenScene(final SceneManager sceneManager,
                                     final EntityFactory entityFactory,
                                     final UiConsoleComponentFactory uiConsoleComponentFactory) {
        final Entity uiManager = entityFactory.create("UI Manager")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(new UiManagerComponent());

        final Entity commandConsole = entityFactory.create("Command Console")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(uiConsoleComponentFactory.buildDefaultUiConsoleComponent());

        final Scene scene = sceneManager.getSceneFactory()
                .createScene("TitleScene")
                .addEntity(uiManager)
                .addEntity(commandConsole);
        sceneManager.addScene(scene);
    }


    private void addStartScene(final EntityFactory entityFactory,
                               final TextureFactory textureFactory,
                               final SceneManager sceneManager) throws IOException {
        final Texture playerTexture;
        try (final InputStream playerTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/link.png")) {
            playerTexture = textureFactory.loadTextureFromPng(playerTextureInputStream);
        }


        final SpriteAnimation moveUpAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(30f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(60f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(90f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(120f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(150f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(180f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(210f, 150.0f, 30f, 30f))
                .build();

        final SpriteAnimation moveEastAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(240f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(270f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(300f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(330f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(360f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(390f, 150.0f, 30f, 30f))
                .build();

        final SpriteAnimation moveWestAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(240f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(270f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(300f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(330f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(360f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(390f, 240.0f, 30f, 30f))
                .build();


        final SpriteAnimation moveDownAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(30f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(60f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(90f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(120f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(150f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(180f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(210f, 240.0f, 30f, 30f))
                .build();



        //Create components
        final KeyboardMovementComponent keyboardMovementComponent = new KeyboardMovementComponent(.2f);
        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 30f, 30f), 90f, 90f);
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

        final Sprite sprite2 = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 30f, 30f), 90f, 90f);
        final Entity networkEntity = entityFactory.create("Network manager")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(new NetworkManagerComponent(() -> playerEntity, () -> entityFactory.create("Player2")
                        .addComponent(new NetworkTransformComponent(false))
                        .addComponent(new AnimatedSpriteRendererComponent.Builder(sprite2)
                                .onEventStopAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT)
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH,
                                        new SpriteAnimation.Builder(moveUpAnimation).build())
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST,
                                        new SpriteAnimation.Builder(moveEastAnimation).build())
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST,
                                        new SpriteAnimation.Builder(moveWestAnimation).build())
                                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH,
                                        new SpriteAnimation.Builder(moveDownAnimation).build())
                                .build())));


        final Scene scene = sceneManager.getSceneFactory().createScene("StartScene")
                .addEntity(networkEntity);
        sceneManager.addScene(scene);
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
        sceneManager.addScene(scene);

        //Create random wood tile
        final Sprite woodSprite = new Sprite(tileTexture, new Rectangle(0.0f, 0.0f, 16f, 16f), 64f, 64f);
        final SpriteRendererComponent spriteRendererComponent = new SpriteRendererComponent(woodSprite);

        final Entity tileEntity = entityFactory.create("Wood")
                .addComponent(spriteRendererComponent);
        scene.addEntity(tileEntity);


        final Entity tilemapEntity = entityFactory.create("Tilemap")
                .addComponent(new TilemapComponent("tilemaps/test-world.json"))
                .addComponent(new TilemapRendererComponent());

        scene.addEntity(tilemapEntity);

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