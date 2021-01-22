package de.codingair.warpsystem.velocity.base.managers;

import de.codingair.warpsystem.core.proxy.base.handlers.WorldHandler;
import de.codingair.warpsystem.velocity.api.files.ConfigFile;
import de.codingair.warpsystem.velocity.api.files.VelocityConfigMask;
import de.codingair.warpsystem.velocity.base.WarpSystem;

public class WorldManager extends WorldHandler {
    public void load() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(WorldHandler.configFile, "/");
        super.load(new VelocityConfigMask(file));
    }

    public void save() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(WorldHandler.configFile);
        VelocityConfigMask writer = new VelocityConfigMask(file);
        super.save(writer);
        file.save();
    }
}
