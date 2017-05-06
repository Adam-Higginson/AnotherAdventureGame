import com.adam.adventure.event.Event;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventType;
import com.adam.adventure.event.LoggingEventListener;
import com.adam.adventure.loop.GameLoop;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.loop.LoopIterationImpl;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.TileRenderable;
import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.shader.ProgramFactory;
import com.adam.adventure.render.shader.Shader;
import com.adam.adventure.render.shader.ShaderCompiler;
import com.adam.adventure.state.GameStateMachine;
import com.adam.adventure.state.LoggingGameStateListener;
import com.adam.adventure.window.Window;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
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

        //Compile shaders
        final ShaderCompiler shaderCompiler = new ShaderCompiler();
        final String vertexShaderSource = readShaderSource("testVert.glsl");
        final Shader vertexShader = shaderCompiler.compileVertexShader(vertexShaderSource);

        final String fragmentShaderSource = readShaderSource("testFrag.glsl");
        final Shader fragmentShader = shaderCompiler.compileFragmentShader(fragmentShaderSource);

        final ProgramFactory programFactory = new ProgramFactory();
        final Program program = programFactory.createProgramFromShaders(vertexShader, fragmentShader);
        program.useProgram();

        //Prepare rendering pipeline
        final RenderQueue renderQueue = new RenderQueue();
        final Renderer renderer = new Renderer(renderQueue, window);

        //Create a test object to render
        final TileRenderable tileRenderable = renderer.buildRenderable(TileRenderable::new);
        renderQueue.addRenderable(tileRenderable);


        eventBus.publishEvent(new Event(EventType.LOADED));
        loop(renderer, window);
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

    private void loop(final Renderer renderer, final Window window) {
        final LoopIteration loopIteration = new LoopIterationImpl(renderer);

        GameLoop.loopUntil(window::shouldClose)
                .andUponEachLoopIterationPerform(loopIteration)
                .loop();
    }


    public static void main(final String[] args) throws IOException {
        new Main().run();
    }
}