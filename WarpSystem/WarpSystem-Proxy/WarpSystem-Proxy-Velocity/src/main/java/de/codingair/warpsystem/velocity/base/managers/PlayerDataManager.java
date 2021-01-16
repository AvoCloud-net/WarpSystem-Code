package de.codingair.warpsystem.velocity.base.managers;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import de.codingair.warpsystem.proxy.core.base.handlers.PlayerDataHandler;
import de.codingair.warpsystem.velocity.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.velocity.utils.VelocityPlayer;
import de.codingair.warpsystem.velocity.utils.VelocityServer;

public class PlayerDataManager extends PlayerDataHandler {
    public PlayerDataManager() {
        super();
    }

    @Subscribe
    public void onConnect(ServerProvideOptionsEvent e) {
        super.onServerProvideOptions(e.getServer());
    }

    @Subscribe
    public void onConnect(ServerConnectedEvent e) {
        super.onConnect(new VelocityPlayer(e.getPlayer()), new VelocityServer(e.getServer()));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent e) {
        super.playerDisconnect(new VelocityPlayer(e.getPlayer()));
    }

    @Subscribe
    public void onSwitch(ServerConnectedEvent e) {
        if (e.getPreviousServer().isPresent()) super.onSwitch(new VelocityPlayer(e.getPlayer()), new VelocityServer(e.getServer()));
    }
}
