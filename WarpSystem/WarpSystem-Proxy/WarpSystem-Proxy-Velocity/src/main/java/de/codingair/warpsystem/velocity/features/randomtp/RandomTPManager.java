package de.codingair.warpsystem.velocity.features.randomtp;

import de.codingair.warpsystem.core.proxy.features.RandomTPHandler;
import de.codingair.warpsystem.velocity.api.files.ConfigFile;
import de.codingair.warpsystem.velocity.api.files.VelocityConfigMask;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.features.FeatureType;

public class RandomTPManager extends RandomTPHandler {
    private ConfigFile file;

    public static RandomTPManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.RANDOM_TP);
    }

    @Override
    public boolean load(boolean loader) {
        WarpSystem.proxy().getEventManager().register(WarpSystem.getInstance(), new RandomTPListener());
        return super.load(loader, new VelocityConfigMask(WarpSystem.getInstance().getFileManager().getFile("RTP_Queue", "/")), new VelocityConfigMask(this.file = WarpSystem.getInstance().getFileManager().getFile("RTP_Worlds", "/")));
    }

    @Override
    public void save(boolean saver) {
        super.save(saver, new VelocityConfigMask(this.file));
        this.file.save();
    }

    public void saveQueue(boolean saver) {
        ConfigFile queue = WarpSystem.getInstance().getFileManager().getFile("RTP_Queue");
        super.saveQueue(saver, new VelocityConfigMask(queue));
        queue.save();
    }
}
