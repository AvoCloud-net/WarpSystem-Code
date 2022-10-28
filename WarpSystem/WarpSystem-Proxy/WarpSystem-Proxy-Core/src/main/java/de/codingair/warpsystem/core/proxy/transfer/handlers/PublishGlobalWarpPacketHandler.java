package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.features.GlobalWarpHandler;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.proxy.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.core.transfer.utils.serializeable.SGlobalWarp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PublishGlobalWarpPacketHandler implements ResponsiblePacketHandler<PublishGlobalWarpPacket, BooleanPacket> {

    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull PublishGlobalWarpPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if(direction == Direction.DOWN && connection instanceof Server<?>) {
            Server<?> server = (Server<?>) connection;

            GlobalWarpHandler handler = Core.getPlugin().getHandler(GlobalWarpHandler.class);
            boolean overwrite = packet.isOverwrite();

            // Set server name before saving it to files!
            packet.warp.setServer(server.getName());

            if (overwrite) {
                SGlobalWarp warp = handler.get(packet.warp.getName());
                if (warp != null) {
                    //Changed
                    warp.setLoc(packet.warp.getLoc());
                    warp.setServer(server.getName());
                    handler.synchronize(warp, UpdateGlobalWarpPacket.Action.UPDATE_POSITION);
                    return CompletableFuture.completedFuture(new BooleanPacket(true));
                } else {
                    //Name does not exist -> create
                    if (handler.add(packet.warp)) {
                        //Added
                        handler.synchronize(packet.warp, UpdateGlobalWarpPacket.Action.ADD);
                        return CompletableFuture.completedFuture(new BooleanPacket(true));
                    }

                    return CompletableFuture.completedFuture(new BooleanPacket(false));
                }
            } else {
                if (handler.add(packet.warp)) {
                    //Added
                    handler.synchronize(packet.warp, UpdateGlobalWarpPacket.Action.ADD);
                    return CompletableFuture.completedFuture(new BooleanPacket(true));
                } else {
                    //Name already exists
                    return CompletableFuture.completedFuture(new BooleanPacket(false));
                }
            }
        } else return CompletableFuture.completedFuture(new BooleanPacket(false));
    }
}
