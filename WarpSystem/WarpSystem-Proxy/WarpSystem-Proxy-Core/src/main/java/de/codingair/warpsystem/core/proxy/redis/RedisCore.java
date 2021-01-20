package de.codingair.warpsystem.core.proxy.redis;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.core.proxy.Core;

public class RedisCore {
    public static final long TIME_OUT = 40;
    private static final RedisCore INSTANCE = new RedisCore();
    private RedisHandler handler;

    private RedisCore() {
    }

    public RedisHandler getHandler() {
        return handler;
    }

    public void setHandler(RedisHandler handler) {
        this.handler = handler;
        this.handler.setSink((data, source) -> Core.getPlugin().dataHandler().receive(data, null, Direction.UP));
    }

    public static RedisCore core() {
        return INSTANCE;
    }
}
