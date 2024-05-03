package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.proxy.features.SpawnHandler;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportSpawnPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TeleportSpawnPacketHandler implements PacketHandler<TeleportSpawnPacket> {
    @Override
    public void process(@NotNull TeleportSpawnPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        SpawnHandler handler = Core.getPlugin().getHandler(SpawnHandler.class);
        Player player = Core.getPlugin().getPlayer(packet.getPlayer());
        Server<?> server = Core.getPlugin().getServer(packet.isRespawn() ? handler.getRespawnServerCommand() : handler.getSpawnServerCommand());

        if (player != null && server != null) {
            if (Core.getServerManager().isOnline(server)) {
                ServerHandler.sendPlayerTo(player, server).whenComplete((res, t) -> {
                    if(t != null) t.printStackTrace();
                    else if(res.isConnected()) {
                        Core.getPlugin().dataHandler().send(packet, server, Direction.DOWN);
                    }
                });
            } else sendServerIsNotOnline(player);
        }
    }

    public abstract void sendServerIsNotOnline(Player player);
}
