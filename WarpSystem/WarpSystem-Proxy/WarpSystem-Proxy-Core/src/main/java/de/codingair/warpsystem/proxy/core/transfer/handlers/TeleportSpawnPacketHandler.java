package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler;
import de.codingair.warpsystem.proxy.core.features.SpawnHandler;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TeleportSpawnPacketHandler implements PacketHandler<TeleportSpawnPacket> {
    @Override
    public void process(@NotNull TeleportSpawnPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        SpawnHandler handler = Core.getPlugin().getHandler(SpawnHandler.class);
        Player player = Core.getPlugin().getPlayer(packet.getPlayer());
        Server server = Core.getPlugin().getServer(packet.isRespawn() ? handler.getRespawn() : handler.getSpawn());

        if (player != null && server != null) {
            if (Core.getServerManager().isOnline(server)) {
                ServerHandler.sendPlayerTo(server, player, new Callback<Server>() {
                    @Override
                    public void accept(Server server) {
                        Core.getPlugin().dataHandler().send(packet, server, Direction.DOWN);
                    }
                });
            } else sendServerIsNotOnline(player);
        }
    }

    public abstract void sendServerIsNotOnline(Player player);
}
