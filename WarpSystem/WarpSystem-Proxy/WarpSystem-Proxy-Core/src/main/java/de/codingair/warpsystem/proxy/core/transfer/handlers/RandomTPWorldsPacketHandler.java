package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.RandomTPHandler;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

public class RandomTPWorldsPacketHandler implements PacketHandler<RandomTPWorldsPacket> {
    @Override
    public void process(@NotNull RandomTPWorldsPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        RandomTPHandler handler = Core.getPlugin().getHandler(RandomTPHandler.class);
        handler.addWorldData(((Server) connection), packet);
    }
}
