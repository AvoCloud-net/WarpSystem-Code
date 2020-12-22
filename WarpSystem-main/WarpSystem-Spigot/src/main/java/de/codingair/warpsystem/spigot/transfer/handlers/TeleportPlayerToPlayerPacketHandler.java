package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportPlayerToPlayerPacketHandler implements PacketHandler<TeleportPlayerToPlayerPacket> {
    @Override
    public void process(@NotNull TeleportPlayerToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        Player gate = Bukkit.getPlayer(packet.getGate());
        Player player = Bukkit.getPlayer(packet.getPlayer());
        Player other = Bukkit.getPlayer(packet.getTarget());

        if(other == null) return;

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(other.getLocation())), other.getName());
        options.setCosts(Math.max(packet.getCosts(), 0));
        options.setSkip(true);
        options.setConfirmPayment(false);
        options.setOrigin(Origin.TeleportCommand);
        options.setMessage(Lang.getPrefix() + (gate == player ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName())));

        if(gate != null && gate != player && packet.isMessageToGate())
            gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", packet.getPlayer()).replace("%warp%", other.getName()));

        TeleportListener.setSpawnPositionOrTeleport(packet.getPlayer(), options);
    }
}
