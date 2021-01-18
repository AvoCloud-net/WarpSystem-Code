package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.codingapi.tools.Location;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareCoordinationTeleportPacketHandler implements ResponsiblePacketHandler<PrepareCoordinationTeleportPacket, IntegerPacket> {
    @Override
    public void process(@NotNull PrepareCoordinationTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportOptions options = new TeleportOptions(new Location(packet.getWorld(), packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch()), packet.getDestinationName());
        if (packet.getMessage() != null) {
            if (packet.getMessage().equals(PrepareCoordinationTeleportPacket.NO_MESSAGE)) options.setMessage(null);
            else options.setMessage(packet.getMessage().replace("%warp%", packet.getDestinationName()));
        }

        TeleportListener.setSpawnPositionOrTeleport(packet.getPlayer(), options);
    }

    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareCoordinationTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        process(packet, proxy, connection, direction);
        return CompletableFuture.completedFuture(new IntegerPacket(0));
    }
}
