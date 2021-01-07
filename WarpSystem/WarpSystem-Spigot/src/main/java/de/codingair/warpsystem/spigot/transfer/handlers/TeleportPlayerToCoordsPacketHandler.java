package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
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

import java.text.DecimalFormat;

public class TeleportPlayerToCoordsPacketHandler implements PacketHandler<TeleportPlayerToCoordsPacket> {
    @Override
    public void process(@NotNull TeleportPlayerToCoordsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player gate = Bukkit.getPlayer(packet.getGate());
        Player player = Bukkit.getPlayer(packet.getPlayer());
        String world = packet.getWorld() != null ? packet.getWorld() : player == null ? gate.getWorld().getName() : player.getWorld().getName();
        double x = (packet.isRelativeX() ? gate.getLocation().getX() : 0) + packet.getX();
        double y = (packet.isRelativeY() ? gate.getLocation().getY() : 0) + packet.getY();
        double z = (packet.isRelativeZ() ? gate.getLocation().getZ() : 0) + packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();
        String destination = packet.getDestinationName() != null ? packet.getDestinationName() : "x=" + cut(x) + ", y=" + cut(y) + ", z=" + cut(z);

        de.codingair.codingapi.tools.Location l = new de.codingair.codingapi.tools.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(l)), destination);

        if(packet.getGate() != null && packet.getPlayer() != null) {
            if(!packet.getGate().equals(packet.getPlayer())) {
                BungeePlayer end = new BungeePlayer(packet.getGate(), packet.getGate());
                end.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", packet.getPlayer()).replace("%warp%", "x=" + cut(x) + ", y=" + cut(y) + ", z=" + cut(z)));
            }

            options.setMessage(Lang.getPrefix() + (packet.getGate().equals(packet.getPlayer()) ? Lang.get("Teleported_To") :
                    Lang.get("Teleported_To_By").replace("%gate%", packet.getGate())));
        }

        if(packet.getDestinationName() == null) options.setOrigin(Origin.TeleportCommand);
        options.setSkip(true);

        TeleportListener.setSpawnPositionOrTeleport(packet.getPlayer(), options);
    }

    private Number cut(double n) {
        double d = Double.parseDouble(new DecimalFormat("#.##").format(n).replace(",", "."));
        if(d == (int) d) return (int) d;
        else return d;
    }
}
