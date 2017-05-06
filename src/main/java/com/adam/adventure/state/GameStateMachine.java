package com.adam.adventure.state;

import com.adam.adventure.event.Event;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventListener;
import com.adam.adventure.event.EventType;
import com.google.common.collect.ImmutableMap;

import java.util.LinkedList;
import java.util.List;

public class GameStateMachine implements EventListener {
    private GameState activeState;
    private final ImmutableMap<EventType, GameState> eventTypeToGameState;
    private final List<GameStateListener> listeners;

    private GameStateMachine() {
        this.activeState = GameState.INITIALISING;
        this.listeners = new LinkedList<>();
        this.eventTypeToGameState = buildEventTypeToGameStateMap();
    }

    private static ImmutableMap<EventType, GameState> buildEventTypeToGameStateMap() {
        return ImmutableMap.<EventType, GameState>builder()
                .put(EventType.INITIALISING, GameState.LOADING)
                .put(EventType.LOADED, GameState.ACTIVE)
                .build();
    }

    public static GameStateMachine registerNewGameStateMachine(final EventBus eventBus) {
        final GameStateMachine gameStateMachine = new GameStateMachine();
        eventBus.registerEventListener(gameStateMachine);
        return gameStateMachine;
    }

    public GameStateMachine addListener(final GameStateListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public void onEvent(final Event event) {
        final GameState newGameState = eventTypeToGameState.get(event.getEventType());
        if (newGameState != null) {
            setGameState(newGameState);
        }
    }


    private void setGameState(final GameState newState) {
        if (activeState != newState) {
            activeState = newState;
            listeners.forEach(listener -> listener.onStateTransition(activeState, newState));
        }
    }
}
