package de.codingair.warpsystem.bungee.base.listeners;

import com.google.common.collect.Iterables;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.PlayerJoinPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.PlayerQuitPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.ProvidePlayerDataPacket;
import de.codingair.warpsystem.base.transfer.utils.PlayerData;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerProvideOptionsEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataListener implements Listener {
    private final ConcurrentHashMap<String, PlayerData> cached = new ConcurrentHashMap<>();

    public PlayerDataListener() {
        WarpSystem.getDataHandler().registerHandler(UpdatePlayerDataPacket.class, (packet, proxy, connection, direction) -> {
            PlayerData cached = this.cached.get(packet.getName().toLowerCase());
            if(cached == null) return;

            packet.update(cached);
        });
    }

    @EventHandler
    public void onConnect(ServerProvideOptionsEvent e) {
        sendNames(e.getInfo());
    }

    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        if(this.cached.putIfAbsent(e.getPlayer().getName().toLowerCase(), new PlayerData(e.getPlayer().getName(), e.getPlayer().getUniqueId(), e.getServer().getInfo().getName())) == null)
            WarpSystem.getInstance().getServerManager().getOnlineServer().forEach(s -> WarpSystem.getDataHandler().send(new PlayerJoinPacket(e.getPlayer().getName(), e.getPlayer().getUniqueId()), s, Direction.DOWN));
    }

    @EventHandler
    public void onConnect(PlayerDisconnectEvent e) {
        if(this.cached.remove(e.getPlayer().getName().toLowerCase()) != null)
            WarpSystem.getInstance().getServerManager().getOnlineServer().forEach(s -> WarpSystem.getDataHandler().send(new PlayerQuitPacket(e.getPlayer().getName()), s, Direction.DOWN));
    }

    @EventHandler
    public void onQuit(ServerSwitchEvent e) {
        PlayerData cached = this.cached.get(e.getPlayer().getName().toLowerCase());
        if(cached == null || !cached.isVanished()) return;

        cached.setVanished(false);
        cached.setServer(e.getPlayer().getServer().getInfo().getName());

        WarpSystem.getInstance().getServerManager().getOnlineServer().forEach(s -> WarpSystem.getDataHandler().send(new UpdatePlayerDataPacket(e.getPlayer().getName()).setVanished(false), s, Direction.DOWN));
    }

    public void sendNames(ServerInfo info) {
        for(Collection<PlayerData> names : Iterables.partition(cached.values(), 256)) {
            WarpSystem.getDataHandler().send(new ProvidePlayerDataPacket(names), info, Direction.DOWN);
        }
    }

    public void onUpdate(UpdatePlayerDataPacket packet, ServerInfo info) {
        PlayerData data = this.cached.get(packet.getName().toLowerCase());
        if(data == null) return;

        packet.update(data);
        WarpSystem.getInstance().getServerManager().getOnlineServer().filter(s -> !s.equals(info)).forEach(s -> WarpSystem.getDataHandler().send(packet, s, Direction.DOWN));
    }

    public boolean isVanished(ProxiedPlayer player) {
        PlayerData cached = this.cached.get(player.getName().toLowerCase());
        return cached != null && cached.isVanished();
    }
}
