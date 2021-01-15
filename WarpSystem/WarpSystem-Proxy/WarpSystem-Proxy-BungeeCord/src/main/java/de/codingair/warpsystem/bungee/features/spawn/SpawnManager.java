package de.codingair.warpsystem.bungee.features.spawn;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.BungeeConfigMask;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.proxy.core.features.SpawnHandler;

public class SpawnManager extends SpawnHandler {

    public static SpawnManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.SPAWN);
    }

    @Override
    public boolean load(boolean loader) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");

        WarpSystem.getInstance().getProxy().getPluginManager().registerListener(WarpSystem.getInstance(), new ServerListener());

        return super.load(loader, new BungeeConfigMask(file));
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");
        super.save(saver, new BungeeConfigMask(file));
        file.save();
    }

    @Override
    public void destroy() {
    }
}
