package de.codingair.warpsystem.bungee.base.managers;

import com.google.common.base.Preconditions;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.base.transfer.utils.serializeable.ServerOptions;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerInitializeEvent;
import de.codingair.warpsystem.bungee.utils.BungeePlayer;
import de.codingair.warpsystem.bungee.utils.BungeeServer;
import de.codingair.warpsystem.proxy.core.utils.Server;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

public class ServerManager extends de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler implements Listener {
    public static void sendPlayerTo(ServerInfo server, ProxiedPlayer player, Callback<ServerInfo> c) {
        Preconditions.checkNotNull(server);
        Preconditions.checkNotNull(player);

        de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler.sendPlayerTo(new BungeeServer(server), new BungeePlayer(player), new Callback<Server>() {
            @Override
            public void accept(Server server) {
                c.accept(((BungeeServer) server).getServer());
            }
        });
    }

    public boolean isOnline(ServerInfo info) {
        return super.isOnline(new BungeeServer(info));
    }

    @Override
    public void triggerServerInitializeEvent(Server server) {
        WarpSystem.getInstance().getProxy().getPluginManager().callEvent(new ServerInitializeEvent(((BungeeServer) server).getServer()));
    }

    public ServerOptions getOptions(ServerInfo info) {
        if (info == null) return null;
        return super.getOptions(new BungeeServer(info));
    }

    public ServerPing getLastPing(ServerInfo info) {
        if (info == null) return null;
        return super.getLastPing(new BungeeServer(info));
    }

    public void applyOptions(ServerInfo info, ServerOptions options) {
        super.applyOptions(new BungeeServer(info), options);
    }
}
