package de.codingair.warpsystem.proxy.core.utils;

import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface Server {
    String getName();

    Stream<Player> getOnlinePlayers();

    int getOnlineCount();

    default boolean isEmpty() {
        return getOnlineCount() == 0;
    }

    CompletableFuture<ServerPing> ping();

    void sendData(String channel, byte[] data);
}
