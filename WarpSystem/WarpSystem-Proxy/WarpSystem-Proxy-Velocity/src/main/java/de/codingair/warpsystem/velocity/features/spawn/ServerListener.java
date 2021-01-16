package de.codingair.warpsystem.velocity.features.spawn;

import com.velocitypowered.api.event.Subscribe;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.base.events.ServerProvideOptionsEvent;

public class ServerListener {

    @Subscribe
    public void onInit(ServerProvideOptionsEvent e) {
        if (!e.getOptions().sameVersion()) return;
        WarpSystem.getInstance().getDataHandler().send(SpawnManager.getInstance().getInfoPacket(), e.getServer(), Direction.DOWN);
    }
}
