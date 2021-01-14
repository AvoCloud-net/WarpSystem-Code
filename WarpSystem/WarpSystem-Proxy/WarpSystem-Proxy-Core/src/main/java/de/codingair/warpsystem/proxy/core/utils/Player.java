package de.codingair.warpsystem.proxy.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Player {
    @NotNull String getName();

    @NotNull UUID getUniqueId();

    Server getServer();

    CompletableFuture<Boolean> connect(Server server);

    void sendGrayMessage(String message);
}
