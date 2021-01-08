package de.codingair.warpsystem.bungee.features.randomtp;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerProvideOptionsEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RandomTPListener implements Listener {

    @EventHandler
    public void onInitialize(ServerProvideOptionsEvent e) {
        if(!e.getOptions().sameVersion()) return;
        RandomTPManager.getInstance().updateQueue(e.getInfo());

        WarpSystem.getDataHandler().send(new RandomTPWorldsPacket(RandomTPManager.getInstance().getWorlds()), e.getInfo(), Direction.DOWN);
    }
}
