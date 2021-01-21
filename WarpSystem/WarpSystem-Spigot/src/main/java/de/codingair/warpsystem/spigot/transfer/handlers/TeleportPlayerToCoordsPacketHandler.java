package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.proxy.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.spigot.api.players.BungeePlayer;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.Lang;
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
        Player player = Bukkit.getPlayer(packet.getPlayer());
        if (player == null) return;

        StringBuilder destination = new StringBuilder();
        String world;
        if(packet.getWorld() != null) {
            world = packet.getWorld();
            destination.append(world);
        } else {
            world = player.getWorld().getName();
        }

        double x = 0, y = 0, z = 0;
        if (packet.getX() != null) {
            x = (packet.isRelativeX() ? player.getLocation().getX() : 0) + packet.getX();
            y = (packet.isRelativeY() ? player.getLocation().getY() : 0) + packet.getY();
            z = (packet.isRelativeZ() ? player.getLocation().getZ() : 0) + packet.getZ();

            if (destination.length() > 0) destination.append(", ");
            if(packet.isRelativeX()) destination.append("x: ").append("~").append(x > 0 ? "+" : "").append(cut(x)).append(", ");
            if(packet.isRelativeY()) destination.append("y: ").append("~").append(y > 0 ? "+" : "").append(cut(y)).append(", ");
            if(packet.isRelativeZ()) destination.append("z: ").append("~").append(z > 0 ? "+" : "").append(cut(z));
        }

        float yaw = 0, pitch = 0;

        if(packet.getYaw() != null) {
            yaw = packet.getYaw();
            pitch = packet.getPitch();

            if (destination.length() > 0) destination.append(", ");
            destination.append("yaw: ").append(cut(yaw)).append(", ");
            destination.append("pitch: ").append(cut(pitch));
        }

        de.codingair.codingapi.tools.Location l = new de.codingair.codingapi.tools.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(l)), destination.toString());

        if (packet.getGate() != null && packet.getPlayer() != null) {
            if (!packet.getGate().equals(packet.getPlayer())) {
                BungeePlayer end = new BungeePlayer(packet.getGate(), packet.getGate());
                end.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", packet.getPlayer()).replace("%warp%", "x=" + cut(x) + ", y=" + cut(y) + ", z=" + cut(z)));
            }

            options.setMessage(Lang.getPrefix() + (packet.getGate().equals(packet.getPlayer()) ? Lang.get("Teleported_To") :
                    Lang.get("Teleported_To_By").replace("%gate%", packet.getGate())));
        }

        if (packet.getDestinationName() == null) options.setOrigin(Origin.TeleportCommand);
        options.setSkip(true);

        TeleportListener.setSpawnPositionOrTeleport(packet.getPlayer(), options);
    }

    private Number cut(double n) {
        double d = Double.parseDouble(new DecimalFormat("#.##").format(n).replace(",", "."));
        if (d == (int) d) return (int) d;
        else return d;
    }
}
