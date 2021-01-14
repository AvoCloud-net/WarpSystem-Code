package de.codingair.warpsystem.bungee.base.listeners;

import de.codingair.warpsystem.base.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.bungee.base.utils.ServerProvideOptionsEvent;
import de.codingair.warpsystem.bungee.utils.BungeePlayer;
import de.codingair.warpsystem.bungee.utils.BungeeServer;
import de.codingair.warpsystem.proxy.core.base.handlers.PlayerDataHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDataListener extends PlayerDataHandler implements Listener {
    public PlayerDataListener() {
        super();
    }

    @EventHandler
    public void onConnect(ServerProvideOptionsEvent e) {
        onServerProvideOptions(new BungeeServer(e.getInfo()));
    }

    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        super.onConnect(new BungeePlayer(e.getPlayer()), new BungeeServer(e.getServer().getInfo()));
    }

    @EventHandler
    public void onConnect(PlayerDisconnectEvent e) {
        super.playerDisconnect(new BungeePlayer(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(ServerSwitchEvent e) {
        super.onSwitch(new BungeePlayer(e.getPlayer()));
    }

    public void onUpdate(UpdatePlayerDataPacket packet, ServerInfo info) {
        super.onUpdate(packet, new BungeeServer(info));
    }

    public boolean isVanished(ProxiedPlayer player) {
        return super.isVanished(new BungeePlayer(player));
    }
}
