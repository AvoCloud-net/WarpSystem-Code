package de.codingair.warpsystem.velocity.features.playerwarps;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import de.codingair.warpsystem.velocity.utils.VelocityPlayer;

public class PlayerWarpListener {
    @Subscribe
    public void onJoin(PostLoginEvent e) {
        PlayerWarpManager.getInstance().checkPlayerWarpOwnerNames(new VelocityPlayer(e.getPlayer()));
    }
}
