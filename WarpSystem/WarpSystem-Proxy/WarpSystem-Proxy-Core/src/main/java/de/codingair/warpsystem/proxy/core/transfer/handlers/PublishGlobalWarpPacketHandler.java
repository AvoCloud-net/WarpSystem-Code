package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.base.transfer.utils.serializeable.SGlobalWarp;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.GlobalWarpHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PublishGlobalWarpPacketHandler implements ResponsiblePacketHandler<PublishGlobalWarpPacket, BooleanPacket> {

    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull PublishGlobalWarpPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        GlobalWarpHandler handler = Core.getPlugin().getHandler(GlobalWarpHandler.class);
        boolean overwrite = packet.isOverwrite();

        if (overwrite) {
            SGlobalWarp warp = handler.get(packet.warp.getName());
            if (warp != null) {
                //Changed
                warp.setLoc(packet.warp.getLoc());
                warp.setServer(packet.warp.getServer());
                handler.synchronize(packet.warp);
                return CompletableFuture.completedFuture(new BooleanPacket(true));
            } else {
                //Name already exists
                return CompletableFuture.completedFuture(new BooleanPacket(false));
            }
        } else {
            if (handler.add(packet.warp)) {
                //Added
                handler.synchronize(packet.warp);
                return CompletableFuture.completedFuture(new BooleanPacket(true));
            } else {
                //Name already exists
                return CompletableFuture.completedFuture(new BooleanPacket(false));
            }
        }
    }
}
