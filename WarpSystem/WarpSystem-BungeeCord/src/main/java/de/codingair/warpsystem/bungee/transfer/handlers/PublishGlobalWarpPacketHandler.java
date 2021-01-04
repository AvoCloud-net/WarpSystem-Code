package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.PublishGlobalWarpPacket;
import de.codingair.warpsystem.base.transfer.serializeable.SGlobalWarp;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PublishGlobalWarpPacketHandler implements ResponsiblePacketHandler<PublishGlobalWarpPacket, BooleanPacket> {

    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull PublishGlobalWarpPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        boolean overwrite = packet.isOverwrite();

        if(overwrite) {
            SGlobalWarp warp = manager.get(packet.warp.getName());
            if(warp != null) {
                //Changed
                warp.setLoc(packet.warp.getLoc());
                warp.setServer(packet.warp.getServer());
                manager.synchronize(packet.warp);
                return CompletableFuture.completedFuture(new BooleanPacket(true));
            } else {
                //Name already exists
                return CompletableFuture.completedFuture(new BooleanPacket(false));
            }
        } else {
            if(manager.add(packet.warp)) {
                //Added
                manager.synchronize(packet.warp);
                return CompletableFuture.completedFuture(new BooleanPacket(true));
            } else {
                //Name already exists
                return CompletableFuture.completedFuture(new BooleanPacket(false));
            }
        }
    }
}
