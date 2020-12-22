package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToggleForceTeleportsPacketHandler implements PacketHandler<ToggleForceTeleportsPacket> {
    @Override
    public void process(@NotNull ToggleForceTeleportsPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        Player player = Bukkit.getPlayer(packet.getPlayer());
        if(player != null) TeleportCommandManager.getInstance().setDenyForceTps(player, packet.isAutoDenyTp());
    }
}
