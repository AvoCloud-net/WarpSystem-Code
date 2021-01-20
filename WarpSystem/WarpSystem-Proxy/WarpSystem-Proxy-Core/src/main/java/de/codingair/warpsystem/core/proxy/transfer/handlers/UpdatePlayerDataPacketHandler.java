package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.general.UpdatePlayerDataPacket;
import org.jetbrains.annotations.NotNull;

public class UpdatePlayerDataPacketHandler implements PacketHandler<UpdatePlayerDataPacket> {
    @Override
    public void process(@NotNull UpdatePlayerDataPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        if (direction == Direction.DOWN) Core.getPlugin().getPlayerData().onUpdate(packet, (Server<?>) connection);
        else if (direction == Direction.UP) Core.getPlugin().getPlayerData().onUpdate(packet);
    }
}
