package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class StartTeleportToPlayerPacketHandler implements ResponsiblePacketHandler<StartTeleportToPlayerPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull StartTeleportToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer player = Players.getPlayer(packet.getPlayer());

        if(player != null) return WarpSystem.getDataHandler().send(new StartTeleportToPlayerPacket(packet.getPlayer(), packet.getTo(), packet.getToDisplayName(), packet.getTeleportRequestSender()), player.getServer().getInfo(), Direction.DOWN);
        else return CompletableFuture.completedFuture(new IntegerPacket(1));
    }
}
