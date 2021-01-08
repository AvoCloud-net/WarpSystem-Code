package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleporterManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomTPWorldsPacketHandler implements PacketHandler<RandomTPWorldsPacket> {
    @Override
    public void process(@NotNull RandomTPWorldsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        RandomTeleporterManager man = RandomTeleporterManager.getInstance();
        if(man == null) return;
        man.updateWorlds(packet.getServer(), packet.getWorlds());
    }
}
