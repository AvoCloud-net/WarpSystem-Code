package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.TeleportHandler;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

public class TeleportCommandOptionsPacketHandler implements PacketHandler<TeleportCommandOptionsPacket> {
    @Override
    public void process(@NotNull TeleportCommandOptionsPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
        handler.registerOptions((Server) connection, packet);
    }
}
