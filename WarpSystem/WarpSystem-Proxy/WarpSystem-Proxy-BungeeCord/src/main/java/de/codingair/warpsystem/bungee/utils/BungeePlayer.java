package de.codingair.warpsystem.bungee.utils;

import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BungeePlayer implements Player {
    private final ProxiedPlayer player;

    public BungeePlayer(@NotNull ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public Server getServer() {
        return new BungeeServer(player.getServer().getInfo());
    }

    @Override
    public CompletableFuture<Boolean> connect(Server server) {
        if (server instanceof BungeeServer) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();

            player.connect(((BungeeServer) server).getServer(), (b, t) -> {
                if (t != null) future.completeExceptionally(t);
                else future.complete(b);
            });

            return future;
        } else return null;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    @Override
    public void sendGrayMessage(String message) {
        TextComponent tc = new TextComponent(message);
        tc.setColor(ChatColor.GRAY);
        player.sendMessage(tc);
    }
}
