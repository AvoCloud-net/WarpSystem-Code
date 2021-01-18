package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareCoordinationTeleportPacketHandler implements ResponsiblePacketHandler<PrepareCoordinationTeleportPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareCoordinationTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player p = Core.getPlugin().getPlayer(packet.getPlayer());
        Server<?> target = Core.getPlugin().getServer(packet.getServer());

        if (p != null && Core.getServerManager().isOnline(target)) {
            if (target.isEmpty()) {
                //switch and teleport
                CompletableFuture<IntegerPacket> future = new CompletableFuture<>();

                p.connect(target).whenComplete((connected, throwable) -> {
                    if (connected) {
                        PrepareCoordinationTeleportPacket finalCall = packet.clone();
                        finalCall.setServer(null);
                        Core.getPlugin().dataHandler().send(finalCall.noFuture(), target, Direction.DOWN);

                        future.complete(new IntegerPacket(0));
                    } else future.complete(new IntegerPacket(1));
                });

                return future;
            } else {
                ServerPing ping = Core.getServerManager().getLastPing(target);

                if (ping == null) {
                    return CompletableFuture.completedFuture(new IntegerPacket(4));
                } else {
                    if (packet.isIgnoreLimit() || target.getOnlineCount() < ping.getMaxPlayers()) {
                        //prepare and switch
                        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();

                        PrepareCoordinationTeleportPacket finalCall = packet.clone();
                        finalCall.setServer(null);

                        Core.getPlugin().dataHandler().send(finalCall, target, Direction.DOWN).thenAccept(deep -> {
                            p.connect(target);
                            future.complete(deep);
                        });

                        return future;
                    } else return CompletableFuture.completedFuture(new IntegerPacket(3));
                }
            }
        } else return CompletableFuture.completedFuture(new IntegerPacket(1));
    }
}
