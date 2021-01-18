package de.codingair.warpsystem.core.proxy.utils;

import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.base.handlers.PlayerDataHandler;
import de.codingair.warpsystem.core.utils.Manager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public interface ProxyPlugin extends Proxy {
    @Nullable Player getPlayer(String name);

    @NotNull Stream<Player> getOnlinePlayers();

    @NotNull Stream<Server<?>> getRegisteredServers();

    @NotNull <D extends DataHandler<Server<?>>> D dataHandler();

    @NotNull ScheduleTask schedule(Runnable runnable, long delay, long interval, TimeUnit unit);

    void runAsync(Runnable runnable);

    @NotNull String getVersion();

    @NotNull File getDataFolder();

    @Nullable InputStream getResourceAsStream(String path);

    void log(String message);

    Server<?> getServer(String server);

    <A extends Manager> A getHandler(Class<A> c);

    PlayerDataHandler getPlayerData();
}
