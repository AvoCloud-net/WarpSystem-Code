package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.base.transfer.utils.serializeable.Serializable;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.PlayerWarpManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SendPlayerWarpsPacketHandler implements PacketHandler<SendPlayerWarpsPacket> {
    @Override
    public void process(@NotNull SendPlayerWarpsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        List<PlayerWarpData> l = packet.getData();

        for(Serializable s : l) {
            PlayerWarpData w = (PlayerWarpData) s;
            PlayerWarpManager.getInstance().updateWarp(w);
        }

        //forwarding
        PlayerWarpManager.getInstance().interactWithServers(s -> {
            if(s.equals(connection)) return;
            WarpSystem.getDataHandler().send(packet, s, Direction.DOWN);
        });
    }
}
