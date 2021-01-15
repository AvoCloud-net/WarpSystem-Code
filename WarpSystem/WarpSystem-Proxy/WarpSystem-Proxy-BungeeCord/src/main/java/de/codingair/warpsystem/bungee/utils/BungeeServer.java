package de.codingair.warpsystem.bungee.utils;

import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BungeeServer implements Server {
    private final ServerInfo server;

    public BungeeServer(@NotNull ServerInfo server) {
        this.server = server;
    }

    @Override
    public String getName() {
        return server.getName();
    }

    @Override
    public Stream<Player> getOnlinePlayers() {
        return server.getPlayers().stream().map(BungeePlayer::new);
    }

    @Override
    public int getOnlineCount() {
        return this.server.getPlayers().size();
    }

    @Override
    public CompletableFuture<ServerPing> ping() {
        CompletableFuture<ServerPing> future = new CompletableFuture<>();
        server.ping((serverPing, t) -> {
            if (t != null) future.completeExceptionally(t);
            else future.complete(new ServerPing(true, serverPing.getPlayers().getOnline(), serverPing.getPlayers().getMax(), BaseComponent.toLegacyText(serverPing.getDescriptionComponent())));
        });
        return future;
    }

    @Override
    public String getMotd() {
        return server.getMotd();
    }

    public ServerInfo getServer() {
        return server;
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.server.sendData(channel, data);
    }
}
