package de.codingair.warpsystem.bungee.features.playerwarps;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.BungeeConfigMask;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.proxy.core.features.PlayerWarpHandler;

public class PlayerWarpManager extends PlayerWarpHandler {
    public static PlayerWarpManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.PLAYER_WARPS);
    }

    @Override
    public boolean load(boolean loader) {
        WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");
        ConfigFile configFile = WarpSystem.getInstance().getFileManager().getFile("Config");

        WarpSystem.getInstance().getProxy().getPluginManager().registerListener(WarpSystem.getInstance(), new PlayerWarpListener());
        return super.load(loader, new BungeeConfigMask(file), new BungeeConfigMask(configFile));
    }

    @Override
    public void save(boolean saver) {
        WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/");
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayerWarps");

        super.save(saver, new BungeeConfigMask(file));
        file.save();
    }
}
