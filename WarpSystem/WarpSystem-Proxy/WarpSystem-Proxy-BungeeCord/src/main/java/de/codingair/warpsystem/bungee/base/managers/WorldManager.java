package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.BungeeConfigMask;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.core.proxy.base.handlers.WorldHandler;

public class WorldManager extends WorldHandler {
    public void load() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().loadFile(WorldHandler.configFile, "/");
        super.load(new BungeeConfigMask(file));
    }

    public void save() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(WorldHandler.configFile);
        BungeeConfigMask writer = new BungeeConfigMask(file);
        super.save(writer);
        file.save();
    }
}
