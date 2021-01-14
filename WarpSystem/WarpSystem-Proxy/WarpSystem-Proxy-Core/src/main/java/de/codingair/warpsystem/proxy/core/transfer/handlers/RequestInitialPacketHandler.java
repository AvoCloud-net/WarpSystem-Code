package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestInitialPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

public class RequestInitialPacketHandler implements PacketHandler<RequestInitialPacket> {
    @Override
    public void process(@NotNull RequestInitialPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        Core.getServerManager().sendInitialPacket((Server) connection);
    }
}
