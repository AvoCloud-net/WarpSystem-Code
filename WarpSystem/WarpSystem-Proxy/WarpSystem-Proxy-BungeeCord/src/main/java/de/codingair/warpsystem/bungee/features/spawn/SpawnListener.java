package de.codingair.warpsystem.bungee.features.spawn;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SpawnListener implements Listener {

    @EventHandler
    public void onInit(ServerConnectEvent e) {
        if (e.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) return;

        if (SpawnManager.getInstance().isSpawnServerProxy()) {
            String spawnServer = SpawnManager.getInstance().getSpawnServerCommand();
            ServerInfo server = WarpSystem.proxy().getServerInfo(spawnServer);
            if (server != null) e.setTarget(server);
        }
    }

}
