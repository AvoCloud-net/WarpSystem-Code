package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.warpsystem.bungee.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.bungee.utils.BungeePlayer;
import de.codingair.warpsystem.bungee.utils.BungeeServer;
import de.codingair.warpsystem.core.proxy.base.handlers.PlayerDataHandler;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDataManager extends PlayerDataHandler implements Listener {
    public PlayerDataManager() {
        super();
    }

    @EventHandler
    public void onConnect(ServerProvideOptionsEvent e) {
        onServerProvideOptions(e.getServer());
    }

    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        super.onConnect(new BungeePlayer(e.getPlayer()), new BungeeServer(e.getServer().getInfo()));
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        super.playerDisconnect(new BungeePlayer(e.getPlayer()));
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent e) {
        super.onSwitch(new BungeePlayer(e.getPlayer()), new BungeeServer(e.getPlayer().getServer().getInfo()));
    }
}
