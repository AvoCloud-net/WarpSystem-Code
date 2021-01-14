package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.proxy.core.features.TeleportHandler;

public class TeleportManager extends TeleportHandler {

    public static TeleportManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TELEPORT);
    }

    @Override
    public boolean load(boolean loader) {
        WarpSystem.proxy().getPluginManager().registerListener(WarpSystem.getInstance(), new TeleportCommandListener());
        return super.load(loader);
    }
}
