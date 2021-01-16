package de.codingair.warpsystem.bungee.features.spawn;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.events.ServerProvideOptionsEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener {

    @EventHandler
    public void onInit(ServerProvideOptionsEvent e) {
        if (!e.getOptions().sameVersion()) return;
        WarpSystem.getDataHandler().send(SpawnManager.getInstance().getInfoPacket(), e.getServer(), Direction.DOWN);
    }
}
