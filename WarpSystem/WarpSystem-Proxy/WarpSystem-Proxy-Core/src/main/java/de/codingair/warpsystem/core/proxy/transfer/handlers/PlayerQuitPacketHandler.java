package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.transfer.packets.proxy.PlayerQuitPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerQuitPacketHandler implements PacketHandler<PlayerQuitPacket> {
    @Override
    public void process(@NotNull PlayerQuitPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if(direction == Direction.UP) {
            Core.getPlugin().getPlayerData().disconnectPlayer(packet);
        }
    }
}
