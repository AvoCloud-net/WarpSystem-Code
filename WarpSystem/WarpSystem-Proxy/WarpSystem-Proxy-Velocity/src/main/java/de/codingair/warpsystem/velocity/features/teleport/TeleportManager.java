package de.codingair.warpsystem.velocity.features.teleport;

import de.codingair.warpsystem.proxy.core.features.TeleportHandler;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.features.FeatureType;

public class TeleportManager extends TeleportHandler {

    public static TeleportManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TELEPORT);
    }

    @Override
    public boolean load(boolean loader) {
        WarpSystem.proxy().getEventManager().register(WarpSystem.getInstance(), new TeleportCommandListener());
        return super.load(loader);
    }
}
