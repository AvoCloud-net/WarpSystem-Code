package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareCoordinationTeleportPacketHandler implements ResponsiblePacketHandler<PrepareCoordinationTeleportPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareCoordinationTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ProxiedPlayer pp = WarpSystem.proxy().getPlayer(packet.getPlayer());
        ServerInfo target = WarpSystem.proxy().getServerInfo(packet.getServer());

        if(WarpSystem.getInstance().getServerManager().isOnline(target)) {
            if(target.getPlayers().isEmpty()) {
                //switch and teleport
                CompletableFuture<IntegerPacket> future = new CompletableFuture<>();

                pp.connect(target, (connected, throwable) -> {
                    if(connected) {
                        PrepareCoordinationTeleportPacket finalCall = packet.clone();
                        finalCall.setServer(null);
                        WarpSystem.getDataHandler().send(finalCall, target, Direction.DOWN);

                        future.complete(new IntegerPacket(0));
                    } else future.complete(new IntegerPacket(1));
                });

                return future;
            } else {
                ServerPing ping = WarpSystem.getInstance().getServerManager().getLastPing(target);

                if(ping == null) {
                    return CompletableFuture.completedFuture(new IntegerPacket(4));
                } else {
                    if(packet.isIgnoreLimit() || target.getPlayers().size() < ping.getMaxPlayers()) {
                        //prepare and switch
                        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();

                        PrepareCoordinationTeleportPacket finalCall = packet.clone();
                        finalCall.setServer(null);

                        WarpSystem.getDataHandler().send(finalCall, target, Direction.DOWN).thenAccept(deep -> {
                            pp.connect(target);
                            future.complete(deep);
                        });

                        return future;
                    } else return CompletableFuture.completedFuture(new IntegerPacket(3));
                }
            }
        } else return CompletableFuture.completedFuture(new IntegerPacket(1));
    }
}
