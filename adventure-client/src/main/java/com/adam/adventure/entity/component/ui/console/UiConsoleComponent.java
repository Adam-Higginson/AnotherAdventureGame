package com.adam.adventure.entity.component.ui.console;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.ui.UiManagerComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.LockInputEvent;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.input.KeyPressListener;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;

public class UiConsoleComponent extends EntityComponent implements KeyPressListener {
    private static final Logger LOG = LoggerFactory.getLogger(UiConsoleComponent.class);

    private static final int CONSOLE_TOGGLE_KEY = GLFW_KEY_F1;
    private static final String CONSOLE_LAYER = "consoleLayer";
    private static final String CONSOLE_PANEL = "consoleParent";
    private static final String CONSOLE_CONTROL = "console";

    @Inject
    private InputManager inputManager;
    @Inject
    private EventBus eventBus;
    @Inject
    private SceneManager sceneManager;

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

        final Optional<UiManagerComponent> uiManagerComponent = getUiManagerComponent();
        if (uiManagerComponent.isPresent()) {
            final Screen baseScreen = uiManagerComponent.get().getBaseScreen();
            final Element baseLayer = baseScreen.findElementById(UiManagerComponent.BASE_LAYER_ID);

            addConsoleLayer(baseLayer);
            addKeyPressListener(baseScreen);
            registerConsoleCommands(baseScreen, uiManagerComponent.get());
            active = true;
        } else {
            LOG.error("No active UIManagerComponent in scene so console could not be activated!");
        }
    }

    @Override
    protected void destroy() {
        eventBus.unsubscribe(this);
        inputManager.removeKeyPressListener(this);

        final Optional<UiManagerComponent> uiManagerComponent = getUiManagerComponent();
        if (uiManagerComponent.isPresent()) {

            closeConsole(uiManagerComponent.get());
            uiManagerComponent.get()
                    .getBaseScreen()
                    .findElementById(UiManagerComponent.BASE_LAYER_ID)
                    .getChildren()
                    .forEach(child -> {
                if (CONSOLE_LAYER.equals(child.getId())) {
                    child.markForRemoval();
                }
            });

            uiManagerComponent.get().getBaseScreen().processAddAndRemoveLayerElements();
            active = false;
        } else {
            LOG.error("Could not destroy UI Console as no UIManagerComponent was found in current scene!");
        }
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
    public void onConsoleMessageEvent(final ConsoleEvent consoleEvent) {
        consoleEvent.handle(this);
    }


    private Optional<UiManagerComponent> getUiManagerComponent() {
        if (sceneManager.getCurrentScene().isEmpty()) {
            return Optional.empty();
        }

        final Scene scene = sceneManager.getCurrentScene().get();
        return scene.getEntityWithComponent(UiManagerComponent.class)
                .flatMap(entity -> entity.getComponent(UiManagerComponent.class));
    }

    private void toggleConsole() {
        final Optional<UiManagerComponent> uiManagerComponent = getUiManagerComponent();
        if (uiManagerComponent.isEmpty()) {
            LOG.error("Attempting to toggle console but no UIManagerComponent present in scene!");
            return;
        }

        if (consoleVisible) {
            closeConsole(uiManagerComponent.get());
            eventBus.publishEvent(new LockInputEvent(this, true));
        } else {
            openConsole(uiManagerComponent.get());
            eventBus.publishEvent(new LockInputEvent(this, false));
        }
    }

    private void openConsole(final UiManagerComponent uiManagerComponent) {
        consoleLayer.showWithoutEffects();
        consoleLayer.startEffect(EffectEventId.onStartScreen, () -> {
            final Screen baseScreen = uiManagerComponent.getBaseScreen();
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

    private void registerConsoleCommands(final Screen baseScreen, final UiManagerComponent uiManagerComponent) {
        console = baseScreen.findNiftyControl("console", Console.class);
        consoleCommands = new ConsoleCommands(uiManagerComponent.getNifty(), console);
        addedConsoleCommands.forEach((text, command) -> consoleCommands.registerCommand(text, args -> command.execute(this, args)));
        consoleCommands.enableCommandCompletion(true);
    }


    private void closeConsole(final UiManagerComponent uiManagerComponent) {
        consoleLayer.startEffect(EffectEventId.onEndScreen, () -> {
            consoleLayer.hideWithoutEffect();
            consoleVisible = false;

            if (oldFocusElement != null) {
                oldFocusElement.setFocus();
            }

            final Screen baseScreen = uiManagerComponent.getBaseScreen();
            baseScreen.getFocusHandler().remove(consoleElementFocus);
        });
    }

}
