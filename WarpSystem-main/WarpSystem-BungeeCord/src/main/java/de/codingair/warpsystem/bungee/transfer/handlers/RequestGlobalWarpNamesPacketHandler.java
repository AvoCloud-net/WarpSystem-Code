package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestGlobalWarpNamesPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.globalwarps.managers.GlobalWarpManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequestGlobalWarpNamesPacketHandler implements PacketHandler<RequestGlobalWarpNamesPacket> {
    @Override
    public void process(@NotNull RequestGlobalWarpNamesPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        GlobalWarpManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        manager.synchronize((ServerInfo) connection);
    }
}
