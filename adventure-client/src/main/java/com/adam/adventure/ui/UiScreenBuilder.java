package com.adam.adventure.ui;

import com.adam.adventure.render.renderable.Renderable;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class UiScreenBuilder implements ScreenController {

    private final Nifty nifty;

    @Inject
    public UiScreenBuilder(final Nifty nifty) {
        this.nifty = nifty;
    }

    public Renderable build() {
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.loadStyleFile("nifty-default-styles.xml");

        nifty.addScreen("start", new ScreenBuilder("start2", this) {{
            layer(new LayerBuilder("consoleLayer") {{
                childLayoutVertical();
                visible(true);
                panel(new PanelBuilder("consoleParent") {{
                    childLayoutCenter();
                    width("95%");
                    height("20%");
                    alignCenter();
                    valignCenter();
                    visible(true);
                    control(new ConsoleBuilder("console") {{
                        width("80%");
                        lines(25);
                        alignCenter();
                        valignCenter();
                    }});
                }});
            }});
        }}.build(nifty));

        final Screen screen = nifty.getScreen("start");
        final Console console = screen.findNiftyControl("console", Console.class);
        console.output("Hello :)");
        nifty.gotoScreen("start");

        return new UiRenderable(nifty);
    }


    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
