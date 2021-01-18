package de.codingair.warpsystem.core.proxy.base.events;

import de.codingair.warpsystem.core.proxy.utils.Server;

public interface IServerInitializeEvent<S extends Server<?>> {
    S getServer();
}
