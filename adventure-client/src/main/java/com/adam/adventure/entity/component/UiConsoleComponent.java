package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.input.KeyPressListener;
import com.adam.adventure.render.ui.UiManager;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;

public class UiConsoleComponent extends EntityComponent implements KeyPressListener {

    private static final int CONSOLE_TOGGLE_KEY = GLFW_KEY_F1;

    private final InputManager inputManager;
    private final UiManager uiManager;
    private boolean consoleVisible;
    private Element consoleLayer;
    private Element consoleElementFocus;
    private Element oldFocusElement;

    public UiConsoleComponent(final InputManager inputManager, final UiManager uiManager) {
        this.inputManager = inputManager;
        this.uiManager = uiManager;
    }

    @Override
    protected void activate() {
        final Screen baseScreen = uiManager.getBaseScreen();
        final Element baseLayer = baseScreen.findElementById(UiManager.BASE_LAYER_ID);

        this.consoleLayer = new LayerBuilder("consoleLayer") {
            {
                childLayoutVertical();
                visible(consoleVisible);
                panel(new PanelBuilder("consoleParent") {{
                    childLayoutCenter();
                    width("50%");
                    height("20%");
                    align(Align.Right);
                    valignCenter();
                    control(new ConsoleBuilder("console") {{
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

        final Element consoleElement = baseScreen.findElementById("console");
        consoleElementFocus = consoleElement.findElementById("#textInput");
        inputManager.addKeyPressListener(this);
    }


    @Override
    public void onKeyPress(final int key) {
        if (key == CONSOLE_TOGGLE_KEY) {
            toggleConsole();
        }
    }

    private void toggleConsole() {
        if (consoleVisible) {
            closeConsole();
        } else {
            openConsole();
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
