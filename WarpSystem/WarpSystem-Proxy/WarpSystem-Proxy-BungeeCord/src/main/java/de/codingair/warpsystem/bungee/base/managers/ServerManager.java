package de.codingair.warpsystem.bungee.base.managers;

import de.codingair.warpsystem.base.transfer.utils.serializeable.ServerOptions;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.events.ServerInitializeEvent;
import de.codingair.warpsystem.bungee.utils.BungeeServer;
import de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler;
import de.codingair.warpsystem.proxy.core.utils.Server;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;

public class ServerManager extends ServerHandler implements Listener {

    @Override
    public void triggerServerInitializeEvent(Server server) {
        WarpSystem.getInstance().getProxy().getPluginManager().callEvent(new ServerInitializeEvent((BungeeServer) server));
    }

    public ServerOptions getOptions(ServerInfo info) {
        if (info == null) return null;
        return super.getOptions(new BungeeServer(info));
    }
}
