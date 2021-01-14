package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.SpawnHandler;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

public class SendGlobalSpawnOptionsPacketHandler implements PacketHandler<SendGlobalSpawnOptionsPacket> {
    @Override
    public void process(@NotNull SendGlobalSpawnOptionsPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        SpawnHandler handler = Core.getPlugin().getHandler(SpawnHandler.class);
        handler.update((Server) connection, packet.getSpawn(), packet.getRespawn());
    }
}
