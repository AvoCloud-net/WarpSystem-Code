package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.bungee.features.spawn.managers.SpawnManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendGlobalSpawnOptionsPacketHandler implements PacketHandler<SendGlobalSpawnOptionsPacket> {
    @Override
    public void process(@NotNull SendGlobalSpawnOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        SpawnManager.getInstance().update((ServerInfo) connection, packet.getSpawn(), packet.getRespawn());
    }
}
