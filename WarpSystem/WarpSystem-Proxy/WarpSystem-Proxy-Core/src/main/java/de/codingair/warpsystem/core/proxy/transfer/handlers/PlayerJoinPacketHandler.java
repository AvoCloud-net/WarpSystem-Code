package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.transfer.packets.proxy.PlayerJoinPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerJoinPacketHandler implements PacketHandler<PlayerJoinPacket> {
    @Override
    public void process(@NotNull PlayerJoinPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if(direction == Direction.UP) {
            Core.getPlugin().getPlayerData().connectPlayer(packet);
        }
    }
}
