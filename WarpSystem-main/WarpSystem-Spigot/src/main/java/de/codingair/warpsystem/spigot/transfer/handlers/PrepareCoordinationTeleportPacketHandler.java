package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.codingapi.tools.Location;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrepareCoordinationTeleportPacketHandler implements PacketHandler<PrepareCoordinationTeleportPacket> {
    @Override
    public void process(@NotNull PrepareCoordinationTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportOptions options = new TeleportOptions(new Location(packet.getWorld(), packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch()), packet.getDestinationName());
        if(packet.getMessage() != null) {
            if(packet.getMessage().equals(PrepareCoordinationTeleportPacket.NO_MESSAGE)) options.setMessage(null);
            else options.setMessage(packet.getMessage().replace("%warp%", packet.getDestinationName()));
        }

        TeleportListener.setSpawnPositionOrTeleport(packet.getPlayer(), options);
    }
}
