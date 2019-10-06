package com.adam.adventure.entity.component.ui.console;

/**
 * Called when the a console command is executed
 */
public interface ConsoleCommand {
    void execute(final UiConsoleComponent console, String... args);
}
