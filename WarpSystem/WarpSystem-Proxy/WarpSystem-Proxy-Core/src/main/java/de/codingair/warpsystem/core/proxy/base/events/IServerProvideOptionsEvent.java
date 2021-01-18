package de.codingair.warpsystem.core.proxy.base.events;

import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.utils.serializeable.ServerOptions;

public interface IServerProvideOptionsEvent<S extends Server<?>> {
    S getServer();

    ServerOptions getOptions();
}
