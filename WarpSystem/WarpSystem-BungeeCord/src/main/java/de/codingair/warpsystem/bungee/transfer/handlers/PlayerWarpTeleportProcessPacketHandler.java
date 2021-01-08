package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.PlayerWarpTeleportProcessPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.PlayerWarpManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerWarpTeleportProcessPacketHandler implements PacketHandler<PlayerWarpTeleportProcessPacket> {
    @Override
    public void process(@NotNull PlayerWarpTeleportProcessPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(packet.getId(), packet.getName());

        if(packet.increaseSales()) w.increaseInactiveSales();
        if(packet.resetSales()) w.setInactiveSales((byte) 0);
        if(packet.increasePerformed()) w.increasePerformed();

        //forwarding
        PlayerWarpManager.getInstance().interactWithServers(s -> {
            if(s.getName().equalsIgnoreCase(((ServerInfo) connection).getName())) return;
            WarpSystem.getDataHandler().send(packet, s, Direction.DOWN);
        });
    }
}
