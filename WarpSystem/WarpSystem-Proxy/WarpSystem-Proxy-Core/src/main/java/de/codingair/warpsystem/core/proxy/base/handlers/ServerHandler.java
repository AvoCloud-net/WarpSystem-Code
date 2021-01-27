package de.codingair.warpsystem.core.proxy.base.handlers;

import com.google.common.base.Preconditions;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.proxy.InitialPacket;
import de.codingair.warpsystem.core.transfer.packets.proxy.SendServerPropertiesPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.core.transfer.utils.serializeable.ServerOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public abstract class ServerHandler {
    private final HashMap<Server<?>, ServerOptions> options = new HashMap<>();
    private final ConcurrentHashMap<Server<?>, ServerPing> cachedPing = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Server<?>, Set<CompletableFuture<Void>>> waiting = new ConcurrentHashMap<>();
    private boolean running = false;

    public static CompletableFuture<SwitchResult> sendPlayerTo(Player player, Server<?> server) {
        Preconditions.checkNotNull(server);
        Preconditions.checkNotNull(player);

        if (player.getServer().equals(server)) {
            return CompletableFuture.completedFuture(SwitchResult.ALREADY_CONNECTED);
        } else if (server.getOnlineCount() == 0) {
            ServerHandler handler = Core.getServerManager();

            //function redirect
            CompletableFuture<SwitchResult> redirect = new CompletableFuture<>();

            //waiting instance
            CompletableFuture<Void> future = new CompletableFuture<>();
            handler.waiting.computeIfAbsent(server, (s) -> new HashSet<>()).add(future);

            player.connect(server).whenComplete((res, t) -> {
                if (t != null) {
                    //error -> return
                    t.printStackTrace();
                    removeFuture(server, future);
                    redirect.completeExceptionally(t);
                } else if (res == null || !res.isConnected()) {
                    //failure -> return
                    removeFuture(server, future);
                    redirect.complete(res);
                } else {
                    future.thenAccept(v -> {
                        //wait for instantiation
                        redirect.complete(res);
                    });
                }
            });

            return redirect;
        } else {
            return player.connect(server);
        }
    }

    private static synchronized void removeFuture(Server<?> server, CompletableFuture<Void> future) {
        Set<CompletableFuture<Void>> set = Core.getServerManager().waiting.get(server);
        if (set != null) set.remove(future);
    }

    public Stream<Server<?>> getOnlineServer() {
        return cachedPing.entrySet().stream().filter((e) -> e.getValue() != null && e.getValue().getStatus()).map(Map.Entry::getKey);
    }

    public boolean isOnline(Server<?> info) {
        ServerPing ping = cachedPing.getOrDefault(info, null);
        return ping != null && ping.getStatus();
    }

    public void run() {
        if (running) return;
        running = true;

        Core.getPlugin().schedule(() -> Core.getPlugin().getRegisteredServers().forEach(info -> info.ping().whenComplete((serverPing, error) -> cachedPing.compute(info, (server, ping) -> {
            if (ping == null) ping = new ServerPing(false, 0, 0, null);

            if (error == null) {
                ping.setStatus(true);
                ping.setPlayers(serverPing.getPlayers());
                ping.setMaxPlayers(serverPing.getMaxPlayers());
                ping.setMotd(serverPing.getMotd());
            } else {
                ping.setStatus(false);
                ping.setPlayers(0);
                ping.setMaxPlayers(0);
                ping.setMotd(null);
            }

            return ping;
        }))), 0, 5, TimeUnit.SECONDS);

        Core.getPlugin().schedule(() -> {
            HashMap<String, ServerPing> copy = new HashMap<>();
            for (Map.Entry<Server<?>, ServerPing> e : cachedPing.entrySet()) {
                copy.put(e.getKey().getName().toLowerCase(), new ServerPing(e.getValue()));
            }

            SendServerPropertiesPacket p = new SendServerPropertiesPacket(copy);
            Core.getPlugin().getRegisteredServers().forEach(target -> {
                if (!target.isEmpty()) {
                    Core.getPlugin().dataHandler().send(p, target, Direction.DOWN);
                }
            });
        }, 1, 5, TimeUnit.SECONDS);
    }

    public void sendInitialPacket(Server<?> server) {
        Core.getPlugin().dataHandler().send(new InitialPacket(Core.getPlugin().getVersion(), server.getName()), server, Direction.DOWN);
        triggerServerInitializeEvent(server);

        Set<CompletableFuture<Void>> l = Core.getServerManager().waiting.remove(server);
        if (l != null) {
            l.removeIf(future -> {
                future.complete(null);
                return true;
            });
        }
    }

    public abstract void triggerServerInitializeEvent(Server<?> server);

    public ServerOptions getOptions(Server<?> info) {
        if (info == null) return null;
        return options.get(info);
    }

    public ServerPing getLastPing(Server<?> info) {
        if (info == null) return null;
        return cachedPing.get(info);
    }

    public void applyOptions(Server<?> info, ServerOptions options) {
        this.options.putIfAbsent(info, options);
    }

    public enum SwitchResult {
        SUCCESS(true),
        FAIL(false),
        CANCELLED(false),
        ALREADY_CONNECTED(true),
        ALREADY_CONNECTING(true),
        ;

        private final boolean connected;

        SwitchResult(boolean connected) {
            this.connected = connected;
        }

        public boolean isConnected() {
            return connected;
        }
    }
}
