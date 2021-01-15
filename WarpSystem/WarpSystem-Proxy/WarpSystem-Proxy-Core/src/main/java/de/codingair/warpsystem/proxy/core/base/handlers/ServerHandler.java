package de.codingair.warpsystem.proxy.core.base.handlers;

import com.google.common.base.Preconditions;
import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.proxy.InitialPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.SendServerPropertiesPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.base.transfer.utils.serializeable.ServerOptions;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public abstract class ServerHandler {
    private final HashMap<Server, ServerOptions> options = new HashMap<>();
    private final ConcurrentHashMap<Server, ServerPing> cachedPing = new ConcurrentHashMap<>();
    private final HashMap<Server, List<Callback<Server>>> waiting = new HashMap<>();
    private boolean running = false;

    public static void sendPlayerTo(Server server, Player player, Callback<Server> c) {
        Preconditions.checkNotNull(server);
        Preconditions.checkNotNull(player);

        if (player.getServer().equals(server)) {
            c.accept(server);
        } else {
            if (server.isEmpty()) addCallbackTo(server, c);
            else c.accept(server);
            player.connect(server);
        }
    }

    private static void addCallbackTo(Server info, Callback<Server> c) {
        List<Callback<Server>> l = Core.getServerManager().waiting.computeIfAbsent(info, k -> new ArrayList<>());
        l.add(c);
    }

    public Stream<Server> getOnlineServer() {
        return cachedPing.entrySet().stream().filter((e) -> e.getValue() != null && e.getValue().getStatus()).map(Map.Entry::getKey);
    }

    public boolean isOnline(Server info) {
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
            for (Map.Entry<Server, ServerPing> e : cachedPing.entrySet()) {
                copy.put(e.getKey().getName().toLowerCase(), new ServerPing(e.getValue()));
            }

            SendServerPropertiesPacket p = new SendServerPropertiesPacket(copy);
            Core.getPlugin().getRegisteredServers().forEach(target -> {
                if (!target.isEmpty()) {
                    Core.getPlugin().dataHandler().send(p, target, Direction.DOWN);
                }
            });
        }, 3, 5, TimeUnit.SECONDS);
    }

    public void sendInitialPacket(Server server) {
        Core.getPlugin().dataHandler().send(new InitialPacket(Core.getPlugin().getVersion(), server.getName()), server, Direction.DOWN);
        triggerServerInitializeEvent(server);

        List<Callback<Server>> l = Core.getServerManager().waiting.remove(server);
        if (l != null) {
            l.forEach(c -> c.accept(server));
            l.clear();
        }
    }

    public abstract void triggerServerInitializeEvent(Server server);

    public ServerOptions getOptions(Server info) {
        if (info == null) return null;
        return options.get(info);
    }

    public ServerPing getLastPing(Server info) {
        if (info == null) return null;
        return cachedPing.get(info);
    }

    public void applyOptions(Server info, ServerOptions options) {
        this.options.putIfAbsent(info, options);
    }
}
