package de.codingair.warpsystem.bungee.features.playerwarps;

import de.codingair.warpsystem.bungee.features.playerwarps.PlayerWarpManager;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerWarpListener implements Listener {
    @EventHandler
    public void onJoin(ServerConnectEvent e) {
        PlayerWarpManager.getInstance().checkPlayerWarpOwnerNames(e.getPlayer());
    }
}
