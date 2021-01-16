package de.codingair.warpsystem.velocity.base.events;

import de.codingair.warpsystem.proxy.core.base.events.IServerInitializeEvent;
import de.codingair.warpsystem.velocity.utils.VelocityServer;

public class ServerInitializeEvent implements IServerInitializeEvent<VelocityServer> {
    private final VelocityServer info;

    public ServerInitializeEvent(VelocityServer info) {
        this.info = info;
    }

    public VelocityServer getServer() {
        return info;
    }
}
