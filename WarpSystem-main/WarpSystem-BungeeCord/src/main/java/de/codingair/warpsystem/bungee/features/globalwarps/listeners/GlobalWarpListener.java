package de.codingair.warpsystem.bungee.features.globalwarps.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GlobalWarpListener implements Listener {
    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        if(e.getServer().getInfo().getPlayers().size() <= 1) {
            //Update it
            GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
            manager.synchronize(e.getServer().getInfo());
        }
    }
}
