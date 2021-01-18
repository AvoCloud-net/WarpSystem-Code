package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportSpawnPacketHandler implements PacketHandler<TeleportSpawnPacket> {
    @Override
    public void process(@NotNull TeleportSpawnPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if (SpawnManager.getInstance() == null) return;
        Spawn spawn = SpawnManager.getInstance().getSpawn();

        if (spawn != null) {
            TeleportOptions options = new TeleportOptions();
            spawn.prepareTeleportOptions(packet.getPlayer(), options);
            if (packet.isRespawn()) options.setMessage(null);

            TeleportListener.setSpawnPositionOrTeleport(packet.getPlayer(), options);
        }
    }
}
