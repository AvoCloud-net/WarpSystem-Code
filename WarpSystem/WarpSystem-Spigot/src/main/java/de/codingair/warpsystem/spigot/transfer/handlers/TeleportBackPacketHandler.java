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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TeleportBackPacketHandler implements ResponsiblePacketHandler<TeleportBackPacket, BytePacket> {
    @Override
    public @NotNull CompletableFuture<BytePacket> response(@NotNull TeleportBackPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if (packet.isSimulate()) {
            CompletableFuture<BytePacket> future = new CompletableFuture<>();
            Location l = TeleportCommandManager.getInstance().getQuitPosition(packet.getName());

            if (l == null) future.complete(new BytePacket(TeleportBackPacket.Result.NO_LAST_POSITION.id()));
            else if (WarpSystem.opt().forbiddenRegion(l)) future.complete(new BytePacket(TeleportBackPacket.Result.PROTECTED_REGION.id()));
            else future.complete(new BytePacket(TeleportBackPacket.Result.SUCCESS.id()));

            return future;
        } else {
            if (TeleportCommandManager.getInstance() == null) return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.PLAYER_NOT_AVAILABLE.id()));
            else {
                Player player = Bukkit.getPlayer(packet.getName());
                if (player != null && !packet.isBypassCooldownCheck() && WarpSystem.cooldown().checkPlayer(player, Origin.TeleportCommand))
                    return CompletableFuture.completedFuture(new BooleanPacket(false));

                TeleportCommandManager.getInstance().teleportToLastBackLocation(packet.getName(), packet.isSwitching(), packet.isForce(), false).thenAccept(result -> {
                    if (result == TeleportBackPacket.Result.SUCCESS) WarpSystem.cooldown().register(player, Origin.TeleportCommand);
                });
            }

            return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.SUCCESS.id()));
        }
    }
}
