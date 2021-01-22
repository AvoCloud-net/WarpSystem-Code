package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.proxy.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.spigot.api.players.ProxyPlayer;
import de.codingair.warpsystem.spigot.base.listeners.TeleportListener;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationAdapter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class TeleportPlayerToCoordsPacketHandler implements PacketHandler<TeleportPlayerToCoordsPacket> {
    @Override
    public void process(@NotNull TeleportPlayerToCoordsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Bukkit.getPlayer(packet.getPlayer());
        if (player == null) return;

        boolean onlySwitch = true;
        StringBuilder destination = new StringBuilder();
        World world = null;

        if (packet.getWorld() != null) {
            world = Bukkit.getWorld(packet.getWorld());
            if (world != null) {
                destination.append(world.getName());
                onlySwitch = false;
            }
        }

        if (world == null) world = player.getWorld();

        double x = 0, y = 0, z = 0;
        if (packet.getX() != null) {
            x = (packet.isRelativeX() ? player.getLocation().getX() : 0) + packet.getX();
            y = (packet.isRelativeY() ? player.getLocation().getY() : 0) + packet.getY();
            z = (packet.isRelativeZ() ? player.getLocation().getZ() : 0) + packet.getZ();

            if (destination.length() > 0) destination.append(", ");

            if (packet.isRelativeZ()) destination.append("x: ").append("~").append(x > 0 ? "+" : "").append(cut(x)).append(", ");
            else destination.append("x: ").append(cut(x)).append(", ");

            if (packet.isRelativeY()) destination.append("y: ").append("~").append(y > 0 ? "+" : "").append(cut(y)).append(", ");
            else destination.append("y: ").append(cut(y)).append(", ");

            if (packet.isRelativeZ()) destination.append("z: ").append("~").append(z > 0 ? "+" : "").append(cut(z));
            else destination.append("z: ").append(cut(z));

            onlySwitch = false;
        } else if (packet.getWorld() != null) {
            Location spawn = world.getSpawnLocation();
            x = spawn.getX();
            y = spawn.getY();
            z = spawn.getZ();
        }

        float yaw, pitch;
        if (packet.getYaw() != null) {
            yaw = packet.getYaw();
            pitch = packet.getPitch();

            if (destination.length() > 0) destination.append(", ");
            destination.append("yaw: ").append(cut(yaw)).append(", ");
            destination.append("pitch: ").append(cut(pitch));
            onlySwitch = false;
        } else {
            yaw = player.getLocation().getYaw();
            pitch = player.getLocation().getPitch();
        }

        if (destination.length() == 0) destination.append(packet.getServer());

        de.codingair.codingapi.tools.Location l = new de.codingair.codingapi.tools.Location(world, x, y, z, yaw, pitch);
        DestinationAdapter adapter;

        if (onlySwitch) adapter = new EmptyAdapter();
        else adapter = new LocationAdapter(l);

        TeleportOptions options = new TeleportOptions(new Destination(adapter), destination.toString());

        if (packet.getGate() != null && packet.getPlayer() != null) {
            if (!packet.getGate().equals(packet.getPlayer())) {
                ProxyPlayer end = new ProxyPlayer(packet.getGate(), packet.getGate());
                end.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", packet.getPlayer()).replace("%warp%", destination.toString()));
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
