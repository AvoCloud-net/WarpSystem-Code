package de.codingair.warpsystem.core.proxy.utils;

import de.codingair.warpsystem.core.proxy.Core;
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

    /**
     * @param command The command line that should be executed.
     * @return true if the command is existing and has been executed.
     */
    default CompletableFuture<Boolean> performCommand(@NotNull String command) {
        return Core.getPlugin().performCommand(this, command);
    }
}
