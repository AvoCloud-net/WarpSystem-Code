package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.PlayerWarpHandler;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

public class DeletePlayerWarpPacketHandler implements PacketHandler<DeletePlayerWarpPacket> {
    @Override
    public void process(@NotNull DeletePlayerWarpPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        PlayerWarpHandler handler = Core.getPlugin().getHandler(PlayerWarpHandler.class);
        PlayerWarpData w = handler.getWarp(packet.getId(), packet.getName());

        handler.delete(w, false);

        //forwarding
        handler.interactWithServers(s -> {
            if (s.getName().equalsIgnoreCase(((Server) connection).getName())) return;
            Core.getPlugin().dataHandler().send(packet, s, Direction.DOWN);
        });
    }
}
