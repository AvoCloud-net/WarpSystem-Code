package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.warpsystem.base.transfer.packets.bungee.PacketVanishInfo;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerInitializeEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class VanishManager implements Listener {
    private final List<String> vanished = new ArrayList<>();

    public VanishManager() {
        WarpSystem.getDataHandler().registerHandler(PacketVanishInfo.class, (packet, proxy, connection) -> {
            if(packet.isVanished()) {
                if(!vanished.contains(packet.getPlayer())) vanished.add(packet.getPlayer().toLowerCase());
            } else vanished.remove(packet.getPlayer());
        });
    }

    @EventHandler
    public void onInit(ServerInitializeEvent e) {
        List<String> l = new ArrayList<>(vanished);
        for(String s : l) {
            ProxiedPlayer p = WarpSystem.proxy().getPlayer(s);
            if(p == null || p.getServer() == null) vanished.remove(s);
            else if(p.getServer().getInfo().equals(e.getInfo())) vanished.remove(s);
        }
        l.clear();
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        vanished.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(ServerSwitchEvent e) {
        vanished.remove(e.getPlayer().getName());
    }

    public List<String> getVanished() {
        return vanished;
    }

    public boolean isVanished(String player) {
        return vanished.contains(player.toLowerCase());
    }
}
