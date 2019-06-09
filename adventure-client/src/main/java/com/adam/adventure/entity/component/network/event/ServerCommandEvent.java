package com.adam.adventure.entity.component.network.event;

import com.adam.adventure.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerCommandEvent extends Event {
    private final String serverCommand;
}
