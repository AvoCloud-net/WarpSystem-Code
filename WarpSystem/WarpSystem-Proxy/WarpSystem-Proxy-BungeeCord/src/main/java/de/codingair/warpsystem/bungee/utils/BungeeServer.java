package de.codingair.warpsystem.bungee.utils;

import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BungeeServer implements Server<String> {
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

    public ServerInfo getServer() {
        return server;
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.server.sendData(channel, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BungeeServer that = (BungeeServer) o;
        return server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server);
    }

    @Override
    public String toString() {
        return "BungeeServer{" +
                "server=" + server +
                '}';
    }
}
