package de.codingair.warpsystem.velocity.features.globalwarps;

import com.velocitypowered.api.event.Subscribe;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.base.events.ServerInitializeEvent;
import de.codingair.warpsystem.velocity.features.FeatureType;

public class GlobalWarpListener {
    @Subscribe
    public void onConnect(ServerInitializeEvent e) {
        GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        manager.synchronize(e.getServer());
    }
}
