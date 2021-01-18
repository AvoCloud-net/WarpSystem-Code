package de.codingair.warpsystem.core.proxy.utils;

import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ServerPing;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface Server<C> {
    String getName();

    Stream<Player> getOnlinePlayers();

    int getOnlineCount();

    default boolean isEmpty() {
        return getOnlineCount() == 0;
    }

    CompletableFuture<ServerPing> ping();

    void sendData(C channel, byte[] data);
}
