package de.codingair.warpsystem.velocity.base.managers;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import de.codingair.warpsystem.core.proxy.base.handlers.CooldownHandler;
import de.codingair.warpsystem.velocity.api.files.ConfigFile;
import de.codingair.warpsystem.velocity.api.files.VelocityConfigMask;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.velocity.utils.VelocityPlayer;
import de.codingair.warpsystem.velocity.utils.VelocityServer;

public class CooldownManager extends CooldownHandler {
    public CooldownManager() {
        super();
    }

    public void load() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(CooldownHandler.configFile, "/");
        super.load(new VelocityConfigMask(file));
    }

    public void save() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(CooldownHandler.configFile);
        VelocityConfigMask writer = new VelocityConfigMask(file);
        super.save(writer);
        file.save();
    }

    @Subscribe
    public void onInit(ServerProvideOptionsEvent e) {
        super.sendData(e.getServer());
    }

    @Subscribe (order = PostOrder.EARLY)
    public void onJoin(ServerConnectedEvent e) {
        super.sendDataFor(new VelocityPlayer(e.getPlayer()), new VelocityServer(e.getServer()));
    }
}
