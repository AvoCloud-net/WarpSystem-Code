package de.codingair.warpsystem.proxy.core.base.events;

import de.codingair.warpsystem.proxy.core.utils.Server;

public interface IServerInitializeEvent<S extends Server<?>> {
    S getServer();
}
