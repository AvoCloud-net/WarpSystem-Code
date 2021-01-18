package de.codingair.warpsystem.velocity.features.globalwarps;

import de.codingair.warpsystem.core.transfer.utils.serializeable.SGlobalWarp;
import de.codingair.warpsystem.core.proxy.features.GlobalWarpHandler;
import de.codingair.warpsystem.velocity.api.files.ConfigFile;
import de.codingair.warpsystem.velocity.api.files.VelocityConfigMask;
import de.codingair.warpsystem.velocity.base.WarpSystem;

public class GlobalWarpManager extends GlobalWarpHandler {

    @Override
    public boolean load(boolean loader) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("GlobalWarps", "/");

        WarpSystem.proxy().getEventManager().register(WarpSystem.getInstance(), new GlobalWarpListener());
        return super.load(loader, new VelocityConfigMask(file));
    }

    @Override
    public void save(SGlobalWarp warp) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("GlobalWarps");
        super.save(warp, new VelocityConfigMask(file));
        file.save();
    }

    @Override
    public void delete(SGlobalWarp warp) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("GlobalWarps");
        super.delete(warp, new VelocityConfigMask(file));
        file.save();
    }
}
