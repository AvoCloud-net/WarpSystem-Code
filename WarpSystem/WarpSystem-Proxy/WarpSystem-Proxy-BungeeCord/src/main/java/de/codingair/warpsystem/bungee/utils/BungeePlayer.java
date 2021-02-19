package de.codingair.warpsystem.bungee.utils;

import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
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
    public BungeeServer getServer() {
        return new BungeeServer(player.getServer().getInfo());
    }

    @Override
    public CompletableFuture<ServerHandler.SwitchResult> connect(Server<?> server) {
        if (server instanceof BungeeServer) {
            CompletableFuture<ServerHandler.SwitchResult> future = new CompletableFuture<>();

            ServerConnectRequest request = ServerConnectRequest.builder()
                    .callback((result, t) -> {
                        if (t != null) future.completeExceptionally(t);
                        else {
                            switch (result) {
                                case SUCCESS:
                                    future.complete(ServerHandler.SwitchResult.SUCCESS);
                                    break;

                                case ALREADY_CONNECTED:
                                    future.complete(ServerHandler.SwitchResult.ALREADY_CONNECTED);
                                    break;

                                case ALREADY_CONNECTING:
                                    future.complete(ServerHandler.SwitchResult.ALREADY_CONNECTING);
                                    break;

                                case EVENT_CANCEL:
                                    future.complete(ServerHandler.SwitchResult.CANCELLED);
                                    break;

                                case FAIL:
                                default:
                                    future.complete(ServerHandler.SwitchResult.FAIL);
                                    break;
                            }
                        }
                    })
                    .reason(ServerConnectEvent.Reason.PLUGIN)
                    .target(((BungeeServer) server).getServer())
                    .build();

            player.connect(request);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BungeePlayer that = (BungeePlayer) o;
        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}
