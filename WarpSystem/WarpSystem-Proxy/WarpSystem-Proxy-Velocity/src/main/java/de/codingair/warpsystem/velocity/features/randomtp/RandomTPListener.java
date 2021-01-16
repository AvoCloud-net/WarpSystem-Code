package de.codingair.warpsystem.velocity.features.randomtp;

import com.velocitypowered.api.event.Subscribe;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.base.events.ServerProvideOptionsEvent;

public class RandomTPListener {

    @Subscribe
    public void onInitialize(ServerProvideOptionsEvent e) {
        if (!e.getOptions().sameVersion()) return;
        RandomTPManager.getInstance().updateQueue(e.getServer());

        WarpSystem.getInstance().getDataHandler().send(new RandomTPWorldsPacket(RandomTPManager.getInstance().getWorlds()), e.getServer(), Direction.DOWN);
    }
}
