package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendGlobalSpawnOptionsPacketHandler implements PacketHandler<SendGlobalSpawnOptionsPacket> {
    @Override
    public void process(@NotNull SendGlobalSpawnOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if(SpawnManager.getInstance() == null) return;
        SpawnManager.getInstance().applyGlobalOptions(packet.getSpawn(), packet.getRespawn());
    }
}
