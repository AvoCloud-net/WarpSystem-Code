package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.bungee.SendGlobalWarpNamesPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;

public class SendGlobalWarpNamesPacketHandler implements PacketHandler<SendGlobalWarpNamesPacket> {
    @Override
    public void process(SendGlobalWarpNamesPacket packet, Proxy proxy) {
        GlobalWarpManager gwManager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS);
        if(packet.isStart()) {
            gwManager.getGlobalWarps().clear();
        }
        gwManager.getGlobalWarps().putAll(packet.getNames());
    }
}
