package de.codingair.warpsystem.proxy.core;

import de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler;
import de.codingair.warpsystem.proxy.core.utils.ProxyPlugin;

public class Core {
    private static final Core CORE = new Core();

    private ProxyPlugin plugin;
    private ServerHandler serverManager;

    private Core() {
    }

    public static ProxyPlugin getPlugin() {
        return CORE.plugin;
    }

    public static void setPlugin(ProxyPlugin plugin) {
        CORE.plugin = plugin;
    }

    public static ServerHandler getServerManager() {
        return CORE.serverManager;
    }

    public static void setServerManager(ServerHandler serverManager) {
        CORE.serverManager = serverManager;
    }
}
