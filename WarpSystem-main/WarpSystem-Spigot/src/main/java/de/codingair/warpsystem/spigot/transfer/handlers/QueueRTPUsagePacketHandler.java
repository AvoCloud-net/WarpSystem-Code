package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.QueueRTPUsagePacket;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class QueueRTPUsagePacketHandler implements PacketHandler<QueueRTPUsagePacket> {
    @Override
    public void process(@NotNull QueueRTPUsagePacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        List<UUID> l = packet.getIds();
        for(UUID uuid : l) {
            RandomTeleporterManager.getInstance().increaseTeleports(uuid);
        }
        l.clear();
    }
}
