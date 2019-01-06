package com.adam.adventure.entity.component.console;

/**
 * Called when the a console command is executed
 */
public interface ConsoleCommand {
    void execute(final UiConsoleComponent console, String... args);
}