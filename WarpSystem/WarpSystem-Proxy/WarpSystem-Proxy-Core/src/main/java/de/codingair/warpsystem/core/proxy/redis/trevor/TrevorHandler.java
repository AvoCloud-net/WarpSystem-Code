package de.codingair.warpsystem.core.proxy.redis.trevor;

import de.codingair.warpsystem.core.proxy.redis.RedisHandler;
import de.codingair.warpsystem.core.proxy.transfer.CoreDataHandler;
import tech.tagline.trevor.api.TrevorService;

public class TrevorHandler extends RedisHandler {

    public TrevorHandler() {
        super(TrevorService.getAPI().getPlatform().getInstanceConfiguration().getID(), CoreDataHandler.redisChannel);
    }

    @Override
    public void send(byte[] data) {
        TrevorService.getAPI().getDatabaseProxy().post(channel, PacketPayload.of(source, data));
    }

    @Override
    public void registerChannel() {
        TrevorService.getAPI().getDatabase().getIntercom().add(channel);
    }

    @Override
    public void unregisterChannel() {
        TrevorService.getAPI().getDatabase().getIntercom().remove(channel);
    }
}
