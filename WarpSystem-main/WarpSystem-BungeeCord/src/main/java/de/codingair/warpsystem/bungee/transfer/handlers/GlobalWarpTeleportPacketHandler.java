package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.GlobalWarpTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.base.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class GlobalWarpTeleportPacketHandler implements ResponsiblePacketHandler<GlobalWarpTeleportPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull GlobalWarpTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        String player = packet.getPlayer();
        String teleport = packet.getId();
        SGlobalWarp warp = manager.get(teleport);
        String teleportDisplayName = packet.getDisplayName();
        if(teleportDisplayName == null) teleportDisplayName = warp.getName();

        if(warp == null) {
            WarpSystem.getInstance().getLogger().log(Level.WARNING, "The server \"" + ((ServerInfo) connection).getName() + "\" is not up to date. Please reload it!");
            return CompletableFuture.completedFuture(new IntegerPacket(1));
        }

        ServerInfo otherServer = WarpSystem.proxy().getServerInfo(warp.getServer());
        ProxiedPlayer p = WarpSystem.proxy().getPlayer(player);

        IntegerPacket answerIntegerPacket = new IntegerPacket();

        if(otherServer == null) return CompletableFuture.completedFuture(new IntegerPacket(2));

        PrepareCoordinationTeleportPacket out = new PrepareCoordinationTeleportPacket(p.getName(), null, warp.getLoc().getWorld(), teleportDisplayName, packet.getMessage() == null ? PrepareCoordinationTeleportPacket.NO_MESSAGE : packet.getMessage(), warp.getLoc().getX(), warp.getLoc().getY(), warp.getLoc().getZ(),
                packet.isKeepRotation() ? -420 : warp.getLoc().getYaw(), packet.isKeepRotation() ? -420 : warp.getLoc().getPitch(), packet.getCosts(), packet.isIgnoreLimit());

        if(p.getServer().getInfo().equals(otherServer)) {
            WarpSystem.getDataHandler().send(out, otherServer, Direction.DOWN);
            return CompletableFuture.completedFuture(new IntegerPacket(0));
        } else {
            if(WarpSystem.getInstance().getServerManager().isOnline(otherServer)) {
                ServerPing ping = WarpSystem.getInstance().getServerManager().getLastPing(otherServer);

                if(ping == null) return CompletableFuture.completedFuture(new IntegerPacket(GlobalWarpTeleportPacket.Result.ERROR.getId()));
                else {
                    if(packet.isIgnoreLimit() || otherServer.getPlayers().size() < ping.getMaxPlayers()) {
                        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();
                        ServerManager.sendPlayerTo(otherServer, p, new Callback<ServerInfo>() {
                            @Override
                            public void accept(ServerInfo object) {
                                WarpSystem.getDataHandler().send(out, otherServer, Direction.DOWN);
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
