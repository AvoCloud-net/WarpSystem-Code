package de.codingair.warpsystem.core.proxy.utils;

import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Player {
    @NotNull String getName();

    @NotNull UUID getUniqueId();

    Server<?> getServer();

    CompletableFuture<ServerHandler.SwitchResult> connect(Server<?> server);

    void sendGrayMessage(String message);
}
