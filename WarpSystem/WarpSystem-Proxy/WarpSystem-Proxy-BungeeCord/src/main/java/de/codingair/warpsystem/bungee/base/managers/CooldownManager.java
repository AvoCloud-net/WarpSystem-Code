package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.codingapi.tools.io.BungeeConfigMask;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.bungee.utils.BungeePlayer;
import de.codingair.warpsystem.bungee.utils.BungeeServer;
import de.codingair.warpsystem.proxy.core.base.handlers.CooldownHandler;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CooldownManager extends CooldownHandler implements Listener {
    public CooldownManager() {
        super();
    }

    public void load() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().loadFile(CooldownHandler.configFile, "/");
        super.load(new BungeeConfigMask(file));
    }

    public void save() {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile(CooldownHandler.configFile);
        BungeeConfigMask writer = new BungeeConfigMask(file);
        super.save(writer);
        file.save();
    }

    @EventHandler
    public void onInit(ServerProvideOptionsEvent e) {
        super.sendData(e.getServer());
    }

    @EventHandler (priority = -64)
    public void onJoin(ServerConnectedEvent e) {
        super.sendDataFor(new BungeePlayer(e.getPlayer()), new BungeeServer(e.getServer().getInfo()));
    }
}
