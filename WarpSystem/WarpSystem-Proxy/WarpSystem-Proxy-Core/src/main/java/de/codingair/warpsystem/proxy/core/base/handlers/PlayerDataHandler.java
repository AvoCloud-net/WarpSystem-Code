package de.codingair.warpsystem.proxy.core.base.handlers;

import com.google.common.collect.Iterables;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.PlayerJoinPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.PlayerQuitPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.ProvidePlayerDataPacket;
import de.codingair.warpsystem.base.transfer.utils.PlayerData;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataHandler {
    private final ConcurrentHashMap<String, PlayerData> cached = new ConcurrentHashMap<>();

    protected PlayerDataHandler() {
        Core.getPlugin().dataHandler().registerHandler(UpdatePlayerDataPacket.class, (packet, proxy, connection, direction) -> {
            PlayerData cached = this.cached.get(packet.getName().toLowerCase());
            if (cached == null) return;

            packet.update(cached);
        });
    }

    protected void onServerProvideOptions(Server<?> s) {
        sendData(s);
    }

    protected void onConnect(Player player, Server<?> server) {
        if (this.cached.putIfAbsent(player.getName().toLowerCase(), new PlayerData(player.getName(), player.getUniqueId(), server.getName())) == null)
            Core.getServerManager().getOnlineServer().forEach(s -> Core.getPlugin().dataHandler().send(new PlayerJoinPacket(player.getName(), server.getName(), player.getUniqueId()), s, Direction.DOWN));
    }

    protected void playerDisconnect(Player player) {
        if (this.cached.remove(player.getName().toLowerCase()) != null)
            Core.getServerManager().getOnlineServer().filter(s -> s.getOnlineCount() > 0).forEach(s -> Core.getPlugin().dataHandler().send(new PlayerQuitPacket(player.getName()), s, Direction.DOWN));
    }

    protected void onSwitch(Player player, Server<?> server) {
        PlayerData cached = this.cached.get(player.getName().toLowerCase());
        if (cached == null || !cached.isVanished()) return;

        cached.setVanished(false);
        cached.setServer(server.getName());

        Core.getServerManager().getOnlineServer().forEach(s -> Core.getPlugin().dataHandler().send(new UpdatePlayerDataPacket(player.getName()).setVanished(false), s, Direction.DOWN));
    }

    private void sendData(Server<?> info) {
        for (Collection<PlayerData> names : Iterables.partition(cached.values(), 256)) {
            Core.getPlugin().dataHandler().send(new ProvidePlayerDataPacket(names), info, Direction.DOWN);
        }
    }

    public void onUpdate(UpdatePlayerDataPacket packet, Server<?> info) {
        PlayerData data = this.cached.get(packet.getName().toLowerCase());
        if (data == null) return;

        packet.update(data);
        Core.getServerManager().getOnlineServer().filter(s -> !s.equals(info)).forEach(s -> Core.getPlugin().dataHandler().send(packet, s, Direction.DOWN));
    }

    public boolean isVanished(Player player) {
        PlayerData cached = this.cached.get(player.getName().toLowerCase());
        return cached != null && cached.isVanished();
    }
}
