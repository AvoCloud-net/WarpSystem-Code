package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.GlobalWarpTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.base.transfer.utils.serializeable.SGlobalWarp;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler;
import de.codingair.warpsystem.proxy.core.features.GlobalWarpHandler;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GlobalWarpTeleportPacketHandler implements ResponsiblePacketHandler<GlobalWarpTeleportPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull GlobalWarpTeleportPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        GlobalWarpHandler handler = Core.getPlugin().getHandler(GlobalWarpHandler.class);
        String player = packet.getPlayer();
        String teleport = packet.getId();
        SGlobalWarp warp = handler.get(teleport);
        String teleportDisplayName = packet.getDisplayName();
        if (teleportDisplayName == null) teleportDisplayName = warp.getName();

        if (warp == null) {
            Core.getPlugin().log("The server \"" + ((Server<?>) connection).getName() + "\" is not up to date. Please reload it!");
            return CompletableFuture.completedFuture(new IntegerPacket(1));
        }

        Server<?> otherServer = Core.getPlugin().getServer(warp.getServer());
        Player p = Core.getPlugin().getPlayer(player);

        if (otherServer == null || p == null) return CompletableFuture.completedFuture(new IntegerPacket(2));

        PrepareCoordinationTeleportPacket out = new PrepareCoordinationTeleportPacket(p.getName(), null, warp.getLoc().getWorld(), teleportDisplayName, packet.getMessage() == null ? PrepareCoordinationTeleportPacket.NO_MESSAGE : packet.getMessage(), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(),
                packet.isKeepRotation() ? -420 : warp.getLoc().getYaw(), packet.isKeepRotation() ? -420 : warp.getLoc().getPitch(), packet.getCosts(), packet.isIgnoreLimit());

        if (p.getServer().equals(otherServer)) {
            Core.getPlugin().dataHandler().send(out.noFuture(), otherServer, Direction.DOWN);
            return CompletableFuture.completedFuture(new IntegerPacket(0));
        } else {
            if (Core.getServerManager().isOnline(otherServer)) {
                ServerPing ping = Core.getServerManager().getLastPing(otherServer);

                if (ping == null) return CompletableFuture.completedFuture(new IntegerPacket(GlobalWarpTeleportPacket.Result.ERROR.getId()));
                else {
                    if (packet.isIgnoreLimit() || otherServer.getOnlineCount() < ping.getMaxPlayers()) {
                        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();
                        ServerHandler.sendPlayerTo(otherServer, p, new Callback<Server<?>>() {
                            @Override
                            public void accept(Server<?> object) {
                                Core.getPlugin().dataHandler().send(out.noFuture(), otherServer, Direction.DOWN);
                                future.complete(new IntegerPacket(0));
                            }
                        });
                        return future;
                    } else return CompletableFuture.completedFuture(new IntegerPacket(GlobalWarpTeleportPacket.Result.SERVER_IS_FULL.getId()));
                }
            } else return CompletableFuture.completedFuture(new IntegerPacket(2));
        }

    }
}
