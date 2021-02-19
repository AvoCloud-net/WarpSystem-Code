package de.codingair.warpsystem.velocity.utils;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.ServerConnection;
import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VelocityPlayer implements Player {
    private final com.velocitypowered.api.proxy.Player player;

    public VelocityPlayer(@NotNull com.velocitypowered.api.proxy.Player player) {
        this.player = player;
    }

    @Override
    public @NotNull String getName() {
        return player.getUsername();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public VelocityServer getServer() {
        ServerConnection server = player.getCurrentServer().orElse(null);
        if (server != null) return new VelocityServer(server.getServer());
        else return null;
    }

    @Override
    public CompletableFuture<ServerHandler.SwitchResult> connect(Server<?> server) {
        if (server instanceof VelocityServer) {
            CompletableFuture<ServerHandler.SwitchResult> future = new CompletableFuture<>();

            ConnectionRequestBuilder builder = player.createConnectionRequest(((VelocityServer) server).getServer());
            builder.connect().whenComplete((b, t) -> {
                if (t != null) future.completeExceptionally(t);
                else {
                    switch (b.getStatus()) {
                        case SUCCESS:
                            future.complete(ServerHandler.SwitchResult.SUCCESS);
                            break;

                        case ALREADY_CONNECTED:
                            future.complete(ServerHandler.SwitchResult.ALREADY_CONNECTED);
                            break;

                        case CONNECTION_IN_PROGRESS:
                            future.complete(ServerHandler.SwitchResult.ALREADY_CONNECTING);
                            break;

                        case CONNECTION_CANCELLED:
                            future.complete(ServerHandler.SwitchResult.CANCELLED);
                            break;

                        case SERVER_DISCONNECTED:
                        default:
                            future.complete(ServerHandler.SwitchResult.FAIL);
                            break;
                    }
                }
            });

            return future;
        } else return null;
    }

    public com.velocitypowered.api.proxy.Player getPlayer() {
        return player;
    }

    @Override
    public void sendGrayMessage(String message) {
        player.sendMessage(Component.text(message).color(TextColor.color(171, 171, 171)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VelocityPlayer that = (VelocityPlayer) o;
        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}
