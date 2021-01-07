package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.DeleteGlobalWarpPacket;
import de.codingair.warpsystem.base.transfer.utils.serializeable.SGlobalWarp;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DeleteGlobalWarpPacketHandler implements ResponsiblePacketHandler<DeleteGlobalWarpPacket, BooleanPacket> {

    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull DeleteGlobalWarpPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        SGlobalWarp warp = manager.get(packet.getWarp());

        if(warp == null) return CompletableFuture.completedFuture(new BooleanPacket(false));
        else {
            manager.remove(warp.getName());
            manager.synchronize(warp);
            return CompletableFuture.completedFuture(new BooleanPacket(true));
        }
    }
}
