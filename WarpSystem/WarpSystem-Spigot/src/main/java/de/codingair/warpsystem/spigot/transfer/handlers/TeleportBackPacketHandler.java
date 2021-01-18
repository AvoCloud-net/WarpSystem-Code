package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportBackPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TeleportBackPacketHandler implements ResponsiblePacketHandler<TeleportBackPacket, BooleanPacket> {
    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull TeleportBackPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Bukkit.getPlayer(packet.getName());

        if(TeleportCommandManager.getInstance() == null || player == null) return CompletableFuture.completedFuture(new BooleanPacket(false));
        else {
            if (!TeleportCommandManager.getInstance().teleportToLastBackLocation(player)) return CompletableFuture.completedFuture(new BooleanPacket(false));
            else {
                WarpSystem.cooldown().register(player, Origin.TeleportCommand);
                return CompletableFuture.completedFuture(new BooleanPacket(true));
            }
        }
    }
}
