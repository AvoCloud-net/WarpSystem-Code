package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.transfer.packets.proxy.ProvidePlayerDataPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvidePlayerDataPacketHandler implements PacketHandler<ProvidePlayerDataPacket> {
    @Override
    public void process(@NotNull ProvidePlayerDataPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if(direction == Direction.UP) {
            //redis
            Core.getPlugin().getPlayerData().apply(packet);
        }
    }
}
