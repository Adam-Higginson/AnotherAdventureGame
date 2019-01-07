package com.adam.adventure.entity.component.console;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.LockInputEvent;
import com.adam.adventure.event.WriteUiConsoleErrorEvent;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.input.KeyPressListener;
import com.adam.adventure.render.ui.UiManager;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleCommands;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;

public class UiConsoleComponent extends EntityComponent implements KeyPressListener {
    private static final int CONSOLE_TOGGLE_KEY = GLFW_KEY_F1;
    private static final String CONSOLE_LAYER = "consoleLayer";
    private static final String CONSOLE_PANEL = "consoleParent";
    private static final String CONSOLE_CONTROL = "console";

    @Inject
    private InputManager inputManager;
    @Inject
    private UiManager uiManager;
    @Inject
    private EventBus eventBus;

    private final Map<String, ConsoleCommand> addedConsoleCommands;
    private ConsoleCommands consoleCommands;
    private boolean active;
    private boolean consoleVisible;
    private Element consoleLayer;
    private Element consoleElementFocus;
    private Element oldFocusElement;
    private Console console;

    public UiConsoleComponent() {
        this.addedConsoleCommands = new HashMap<>();
    }


    @Override
    protected void activate() {
        eventBus.register(this);

        final Screen baseScreen = uiManager.getBaseScreen();
        final Element baseLayer = baseScreen.findElementById(UiManager.BASE_LAYER_ID);

        addConsoleLayer(baseLayer);
        addKeyPressListener(baseScreen);
        registerConsoleCommands(baseScreen);
        active = true;
    }

    @Override
    protected void destroy() {
        //TODO Deregister from event bus

        closeConsole();
        inputManager.removeKeyPressListener(this);

        uiManager.getBaseScreen().findElementById(UiManager.BASE_LAYER_ID).getChildren().forEach(child -> {
            if (CONSOLE_LAYER.equals(child.getId())) {
                child.markForRemoval();
            }
        });

        uiManager.getBaseScreen().processAddAndRemoveLayerElements();

        active = false;
    }

    public UiConsoleComponent addConsoleCommand(final String commandText, final ConsoleCommand consoleCommand) {
        addedConsoleCommands.put(commandText, consoleCommand);
        if (active) {
            consoleCommands.registerCommand(commandText, args -> consoleCommand.execute(this, args));
        }

        return this;
    }

    public UiConsoleComponent writeLine(final String line) {
        console.output(line);
        return this;
    }

    public UiConsoleComponent writeLine(final String line, final Color color) {
        console.output(line, color);
        return this;
    }

    public UiConsoleComponent writeError(final String error) {
        console.outputError(error);
        return this;
    }


    @Override
    public void onKeyPress(final int key) {
        if (key == CONSOLE_TOGGLE_KEY) {
            toggleConsole();
        }
    }

    @EventSubscribe
    public void onConsoleMessageEvent(final WriteUiConsoleErrorEvent writeUiConsoleErrorEvent) {
        writeError(writeUiConsoleErrorEvent.getMessage());
    }

    private void toggleConsole() {
        if (consoleVisible) {
            closeConsole();
            eventBus.publishEvent(new LockInputEvent(this, true));
        } else {
            openConsole();
            eventBus.publishEvent(new LockInputEvent(this, false));
        }
    }

    private void openConsole() {
        consoleLayer.showWithoutEffects();
        consoleLayer.startEffect(EffectEventId.onStartScreen, () -> {
            final Screen baseScreen = uiManager.getBaseScreen();
            oldFocusElement = baseScreen.getFocusHandler().getKeyboardFocusElement();

            // add the consoleElement to the focushandler, when it's not yet added already
            baseScreen.getFocusHandler().addElement(consoleElementFocus);
            consoleElementFocus.setFocus();

            consoleVisible = true;
        });
    }

    private void addKeyPressListener(final Screen baseScreen) {
        final Element consoleElement = baseScreen.findElementById("console");
        consoleElementFocus = consoleElement.findElementById("#textInput");
        inputManager.addKeyPressListener(this);
    }


    @SuppressWarnings("squid:S3599")
    private void addConsoleLayer(final Element baseLayer) {
        this.consoleLayer = new LayerBuilder(CONSOLE_LAYER) {
            {
                childLayoutVertical();
                visible(consoleVisible);
                panel(new PanelBuilder(CONSOLE_PANEL) {{
                    childLayoutCenter();
                    width("70%");
                    height("30%");
                    align(Align.Right);
                    valignCenter();
                    control(new ConsoleBuilder(CONSOLE_CONTROL) {{
                        width("80%");
                        lines(25);
                        alignCenter();
                        valignCenter();
                        onStartScreenEffect(new EffectBuilder("move") {{
                            inherit();
                            effectParameter("direction", "top");
                            effectParameter("mode", "in");
                            effectParameter("length", "500");
                        }});
                        onEndScreenEffect(new EffectBuilder("move") {{
                            inherit();
                            effectParameter("direction", "top");
                            effectParameter("mode", "out");
                            effectParameter("length", "500");
                        }});
                    }});
                }});
            }
        }.build(baseLayer);
    }

    private void registerConsoleCommands(final Screen baseScreen) {
        console = baseScreen.findNiftyControl("console", Console.class);
        consoleCommands = new ConsoleCommands(uiManager.getNifty(), console);
        addedConsoleCommands.forEach((text, command) -> consoleCommands.registerCommand(text, args -> command.execute(this, args)));
        consoleCommands.enableCommandCompletion(true);
    }


    private void closeConsole() {
        consoleLayer.startEffect(EffectEventId.onEndScreen, () -> {
            consoleLayer.hideWithoutEffect();
            consoleVisible = false;

            if (oldFocusElement != null) {
                oldFocusElement.setFocus();
            }

            final Screen baseScreen = uiManager.getBaseScreen();
            baseScreen.getFocusHandler().remove(consoleElementFocus);
        });
    }

}
