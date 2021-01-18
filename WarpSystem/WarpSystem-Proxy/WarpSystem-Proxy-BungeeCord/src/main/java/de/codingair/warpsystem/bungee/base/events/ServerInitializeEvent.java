package de.codingair.warpsystem.bungee.base.events;

import de.codingair.warpsystem.bungee.utils.BungeeServer;
import de.codingair.warpsystem.core.proxy.base.events.IServerInitializeEvent;
import net.md_5.bungee.api.plugin.Event;

public class ServerInitializeEvent extends Event implements IServerInitializeEvent<BungeeServer> {
    private final BungeeServer info;

    public ServerInitializeEvent(BungeeServer info) {
        this.info = info;
    }

    public BungeeServer getServer() {
        return info;
    }
}
