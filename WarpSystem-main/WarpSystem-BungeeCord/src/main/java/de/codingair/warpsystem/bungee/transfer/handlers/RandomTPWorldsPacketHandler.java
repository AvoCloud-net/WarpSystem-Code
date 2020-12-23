package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.bungee.features.randomtp.RandomTPManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomTPWorldsPacketHandler implements PacketHandler<RandomTPWorldsPacket> {
    @Override
    public void process(@NotNull RandomTPWorldsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        RandomTPManager.getInstance().addWorldData(((ServerInfo) connection).getName(), packet.getWorlds());
    }
}
