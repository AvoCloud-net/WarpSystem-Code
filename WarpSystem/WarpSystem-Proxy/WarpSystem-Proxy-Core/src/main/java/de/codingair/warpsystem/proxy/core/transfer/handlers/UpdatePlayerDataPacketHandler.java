package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

public class UpdatePlayerDataPacketHandler implements PacketHandler<UpdatePlayerDataPacket> {
    @Override
    public void process(@NotNull UpdatePlayerDataPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        Core.getPlugin().getPlayerData().onUpdate(packet, (Server) connection);
    }
}
