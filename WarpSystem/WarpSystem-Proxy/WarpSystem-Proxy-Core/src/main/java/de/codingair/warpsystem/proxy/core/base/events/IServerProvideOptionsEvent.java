package de.codingair.warpsystem.proxy.core.base.events;

import de.codingair.warpsystem.base.transfer.utils.serializeable.ServerOptions;
import de.codingair.warpsystem.proxy.core.utils.Server;

public interface IServerProvideOptionsEvent<S extends Server<?>> {
    S getServer();
    ServerOptions getOptions();
}
