package com.adam.adventure.server;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.tilemap.TilemapComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.UncaughtThrowableEvent;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.server.module.AdventureServerModule;
import com.adam.adventure.server.module.ServerDatagramSocket;
import com.adam.adventure.server.player.PlayerLoginCompleter;
import com.adam.adventure.server.receiver.ServerReceiver;
import com.adam.adventure.server.state.WorldStateManager;
import com.adam.adventure.server.tick.ServerTickScheduler;
import com.adam.adventure.server.tick.ServerTickSchedulerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.DatagramSocket;

@Slf4j
public class AdventureServer implements Runnable {

    @CommandLine.Option(names = {"-t", "--tickrate"}, description = "The tickrate of the server", required = true)
    private long tickrate;

    @CommandLine.Option(names = {"-p", "--port"}, description = "The port to bind the server to", required = true)
    private int port;


    private ServerReceiver serverReceiver;
    private Thread serverReceiveThread;
    private ServerTickScheduler serverTickScheduler;
    private DatagramSocket datagramSocket;

    @Override
    public void run() {
        LOG.info("Starting adventure server on port: {} with tickrate: {}", port, tickrate);

        final Injector injector = Guice.createInjector(new AdventureServerModule(port, tickrate));
        //Creates instance and allows it to start listening for events.
        injector.getInstance(PlayerLoginCompleter.class);
        injector.getInstance(WorldStateManager.class);
        injector.getInstance(EventBus.class).register(this);

        addTestScene(injector.getInstance(SceneManager.class), injector.getInstance(EntityFactory.class));

        serverTickScheduler = injector.getInstance(ServerTickSchedulerFactory.class).create(tickrate);
        serverTickScheduler.start();
        serverReceiver = injector.getInstance(ServerReceiver.class);
        serverReceiveThread = new Thread(serverReceiver);
        serverReceiveThread.start();

        datagramSocket = injector.getInstance(Key.get(DatagramSocket.class, ServerDatagramSocket.class));
        addShutdownHook();
        LOG.info("Adventure server started.");

    }

    private void addTestScene(final SceneManager sceneManager, final EntityFactory entityFactory) {
        sceneManager.addScene("Test Scene", () -> {
            final Entity tilemapEntity = entityFactory.create("Tilemap")
                    .addComponent(new TilemapComponent("tilemaps/test-world.json"));

            final Scene scene = sceneManager.getSceneFactory().createScene("Test Scene");
            scene.addEntity(tilemapEntity);

            return scene;
        });
    }


    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @EventSubscribe
    public void onUncaughtThrowable(final UncaughtThrowableEvent uncaughtThrowableEvent) {
        LOG.error("Error when running server!", uncaughtThrowableEvent.getThrowable());
        System.exit(1);
    }


    private void shutdown() {
        try {
            LOG.info("Stopping adventure server...");
            if (datagramSocket != null) {
                datagramSocket.close();
            }

            if (serverReceiver != null) {
                serverReceiver.stop();
            }
            if (serverReceiveThread != null) {
                serverReceiveThread.join(1000);
            }

            if (serverTickScheduler != null) {
                serverTickScheduler.stop();
            }
            LOG.info("Adventure server stopped");
        } catch (final Exception e) {
            LOG.error("Error when stopping adventure server", e);
        }
    }

    public static void main(final String[] args) {
        CommandLine.run(new AdventureServer(), args);
    }
}
