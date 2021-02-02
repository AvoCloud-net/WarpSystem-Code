package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.packets.impl.BytePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportBackPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TeleportBackPacketHandler implements ResponsiblePacketHandler<TeleportBackPacket, BytePacket> {
    @Override
    public @NotNull CompletableFuture<BytePacket> response(@NotNull TeleportBackPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Bukkit.getPlayer(packet.getName());

        if (TeleportCommandManager.getInstance() == null || player == null) return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.PLAYER_NOT_AVAILABLE.id()));
        else {
            if (!packet.isBypassCooldownCheck() && WarpSystem.cooldown().checkPlayer(player, Origin.TeleportCommand)) return CompletableFuture.completedFuture(new BooleanPacket(false));

            if (!TeleportCommandManager.getInstance().teleportToLastBackLocation(player, packet.isSwitching(), packet.isForce())) return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.NO_LAST_POSITION.id()));
            else {
                WarpSystem.cooldown().register(player, Origin.TeleportCommand);
                return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.SUCCESS.id()));
            }
        }
    }
}
