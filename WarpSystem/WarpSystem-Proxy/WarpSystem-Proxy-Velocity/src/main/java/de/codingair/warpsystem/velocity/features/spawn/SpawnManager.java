package de.codingair.warpsystem.velocity.features.spawn;

import de.codingair.warpsystem.core.proxy.features.SpawnHandler;
import de.codingair.warpsystem.velocity.api.files.ConfigFile;
import de.codingair.warpsystem.velocity.api.files.VelocityConfigMask;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.features.FeatureType;

public class SpawnManager extends SpawnHandler {

    public static SpawnManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.SPAWN);
    }

    @Override
    public boolean load(boolean loader) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");

        WarpSystem.proxy().getEventManager().register(WarpSystem.getInstance(), new ServerListener());
        WarpSystem.proxy().getEventManager().register(WarpSystem.getInstance(), new SpawnListener());

        return super.load(loader, new VelocityConfigMask(file));
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        super.save(saver, new VelocityConfigMask(file));
        file.save();
    }

    @Override
    public void destroy() {
    }
}
