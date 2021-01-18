package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.features.TeleportHandler;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportCommandOptionsPacket;
import org.jetbrains.annotations.NotNull;

public class TeleportCommandOptionsPacketHandler implements PacketHandler<TeleportCommandOptionsPacket> {
    @Override
    public void process(@NotNull TeleportCommandOptionsPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
        handler.registerOptions((Server) connection, packet);
    }
}
