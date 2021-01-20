package de.codingair.warpsystem.core.proxy.redis.trevor;

import de.codingair.warpsystem.core.proxy.redis.RedisHandler;
import tech.tagline.trevor.api.TrevorService;

public class TrevorHandler extends RedisHandler {

    public TrevorHandler() {
        super(TrevorService.getAPI().getPlatform().getInstanceConfiguration().getID());
    }

    @Override
    public void send(byte[] data, String channel) {
        TrevorService.getAPI().getDatabaseProxy().post(channel, PacketPayload.of(source, data));
    }

    @Override
    public void registerChannel(String... channel) {
        TrevorService.getAPI().getDatabase().getIntercom().add(channel);
    }

    @Override
    public void unregisterChannel(String... channel) {
        TrevorService.getAPI().getDatabase().getIntercom().remove(channel);
    }
}
