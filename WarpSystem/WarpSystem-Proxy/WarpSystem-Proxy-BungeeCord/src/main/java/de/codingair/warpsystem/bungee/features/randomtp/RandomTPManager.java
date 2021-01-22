package de.codingair.warpsystem.bungee.features.randomtp;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.BungeeConfigMask;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.core.proxy.features.RandomTPHandler;

public class RandomTPManager extends RandomTPHandler {
    private ConfigFile file;

    public static RandomTPManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.RANDOM_TP);
    }

    @Override
    public boolean load(boolean loader) {
        WarpSystem.getInstance().getProxy().getPluginManager().registerListener(WarpSystem.getInstance(), new RandomTPListener());
        return super.load(loader, new BungeeConfigMask(WarpSystem.getInstance().getFileManager().loadFile("RTP_Queue", "/")), new BungeeConfigMask(this.file = WarpSystem.getInstance().getFileManager().loadFile("RTP_Worlds", "/")));
    }

    @Override
    public void save(boolean saver) {
        super.save(saver, new BungeeConfigMask(this.file));
        this.file.save();
    }

    public void saveQueue(boolean saver) {
        ConfigFile queue = WarpSystem.getInstance().getFileManager().getFile("RTP_Queue");
        super.saveQueue(saver, new BungeeConfigMask(queue));
        queue.save();
    }
}
