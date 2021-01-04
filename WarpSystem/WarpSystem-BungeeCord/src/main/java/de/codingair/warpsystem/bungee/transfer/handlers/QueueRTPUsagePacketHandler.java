package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.QueueRTPUsagePacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.randomtp.RandomTPManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QueueRTPUsagePacketHandler implements PacketHandler<QueueRTPUsagePacket> {
    @Override
    public void process(@NotNull QueueRTPUsagePacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ServerInfo server = WarpSystem.proxy().getServerInfo(packet.getServer());
        if(!server.getPlayers().isEmpty()) WarpSystem.getDataHandler().send(packet, server, Direction.DOWN);
        else RandomTPManager.getInstance().addQueueEntry(packet.getIdOnce(), packet.getServer());
    }
}
