package de.codingair.warpsystem.spigot.transfer.spigot;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.variants.OneWayDataHandler;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpigotHandler extends OneWayDataHandler<Player> {
    public SpigotHandler(WarpSystem plugin) {
        super(plugin.getName().toLowerCase(), plugin);
    }

    @Override
    public void registering() {
        for(PacketType value : PacketType.values()) {
            registerPacket(value.getPacket());
        }
    }

    @Override
    protected void send(byte[] data, Player p) {
        if(p == null) p = getAny();
        if(p == null) return; //nobody online
        p.sendPluginMessage(getProxy(), channelProxy, data);
    }

    public void send(@NotNull Packet packet) {
        super.send(packet, null);
    }

    private Player getAny() {
        Optional<? extends Player> opt = Bukkit.getOnlinePlayers().stream().findAny();
        return opt.orElse(null);
    }
}
