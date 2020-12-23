package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.managers.PlayerWarpManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendPlayerWarpUpdatePacketHandler implements PacketHandler<SendPlayerWarpUpdatePacket> {
    @Override
    public void process(@NotNull SendPlayerWarpUpdatePacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        PlayerWarpUpdate update = packet.getUpdate();
        PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(update.getId(), update.getOriginName());
        w.apply(update);

        //forwarding
        PlayerWarpManager.getInstance().interactWithServers(s -> {
            if(s.getName().equalsIgnoreCase(((ServerInfo) connection).getName())) return;
            WarpSystem.getDataHandler().send(packet, s, Direction.DOWN);
        });
        update.destroy();
    }
}
