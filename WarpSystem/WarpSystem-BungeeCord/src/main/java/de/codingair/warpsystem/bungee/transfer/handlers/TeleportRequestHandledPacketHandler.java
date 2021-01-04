package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.TeleportRequestHandledPacket;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportRequestHandledPacketHandler implements PacketHandler<TeleportRequestHandledPacket> {
    @Override
    public void process(@NotNull TeleportRequestHandledPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer sender = Players.getPlayer(packet.getSender());
        if(sender != null) WarpSystem.getDataHandler().send(packet, sender.getServer().getInfo(), Direction.DOWN);
    }
}
