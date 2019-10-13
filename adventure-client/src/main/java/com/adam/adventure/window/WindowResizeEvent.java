package com.adam.adventure.window;

import com.adam.adventure.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WindowResizeEvent extends Event {
    private final int width;
    private final int height;
}
