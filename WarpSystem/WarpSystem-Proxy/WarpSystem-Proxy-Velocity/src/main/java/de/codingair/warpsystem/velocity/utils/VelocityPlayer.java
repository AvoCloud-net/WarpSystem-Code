package de.codingair.warpsystem.velocity.utils;

import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.ServerConnection;
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
    public CompletableFuture<Boolean> connect(Server server) {
        if (server instanceof VelocityServer) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();

            ConnectionRequestBuilder builder = player.createConnectionRequest(((VelocityServer) server).getServer());
            builder.connect().whenComplete((b, t) -> {
                if (t != null) future.completeExceptionally(t);
                else future.complete(b.isSuccessful());
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
}
