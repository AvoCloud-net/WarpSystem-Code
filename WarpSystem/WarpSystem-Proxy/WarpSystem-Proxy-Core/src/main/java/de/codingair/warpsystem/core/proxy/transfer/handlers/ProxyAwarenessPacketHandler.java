package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.redis.RedisCore;
import de.codingair.warpsystem.core.transfer.packets.proxy.ProxyAwarenessPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxyAwarenessPacketHandler implements PacketHandler<ProxyAwarenessPacket> {
    @Override
    public void process(@NotNull ProxyAwarenessPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        RedisCore.recognize(packet.getProxy());
    }
}
