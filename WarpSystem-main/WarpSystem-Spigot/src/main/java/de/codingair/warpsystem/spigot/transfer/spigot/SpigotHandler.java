package de.codingair.warpsystem.spigot.transfer.spigot;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.variants.OneWayDataHandler;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpigotHandler extends OneWayDataHandler<Player> implements PluginMessageListener {
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

    @Override
    public void onPluginMessageReceived(@NotNull String tag, @NotNull Player player, @NotNull byte[] bytes) {
        if(tag.equals(getChannelBackend())) receive(bytes, player);
    }

    public void send(@NotNull Packet packet) {
        super.send(packet, null);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet) {
        return super.send(packet, null);
    }

    private Player getAny() {
        Optional<? extends Player> opt = Bukkit.getOnlinePlayers().stream().findAny();
        return opt.orElse(null);
    }
}
