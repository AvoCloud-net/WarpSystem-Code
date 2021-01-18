package de.codingair.warpsystem.spigot.transfer;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.variants.OneWayDataHandler;
import de.codingair.warpsystem.base.transfer.packets.general.*;
import de.codingair.warpsystem.base.transfer.packets.proxy.*;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.TeleportRequestHandledPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.transfer.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpigotHandler extends OneWayDataHandler<Player> implements PluginMessageListener {
    public SpigotHandler(WarpSystem plugin) {
        super("warpsystem", plugin);
    }

    @Override
    public void registering() {
        for (PacketType value : PacketType.values()) {
            registerPacket(value.getPacket());
        }

        registerHandler(SendGlobalSpawnOptionsPacket.class, new SendGlobalSpawnOptionsPacketHandler());
        registerHandler(TeleportSpawnPacket.class, new TeleportSpawnPacketHandler());
        registerHandler(PrepareCoordinationTeleportPacket.class, new PrepareCoordinationTeleportPacketHandler());
        registerHandler(PrepareTeleportRequestPacket.class, new PrepareTeleportRequestPacketHandler());
        registerHandler(StartTeleportToPlayerPacket.class, new StartTeleportToPlayerPacketHandler());
        registerHandler(TeleportPlayerToCoordsPacket.class, new TeleportPlayerToCoordsPacketHandler());
        registerHandler(TeleportPlayerToPlayerPacket.class, new TeleportPlayerToPlayerPacketHandler());
        registerHandler(TeleportRequestHandledPacket.class, new TeleportRequestHandledPacketHandler());
        registerHandler(ToggleForceTeleportsPacket.class, new ToggleForceTeleportsPacketHandler());
        registerHandler(UpdateGlobalWarpPacket.class, new UpdateGlobalWarpPacketHandler());
        registerHandler(SendGlobalWarpNamesPacket.class, new SendGlobalWarpNamesPacketHandler());
        registerHandler(PlayerJoinPacket.class, new PlayerJoinPacketHandler());
        registerHandler(PlayerQuitPacket.class, new PlayerQuitPacketHandler());
        registerHandler(ProvidePlayerDataPacket.class, new ProvideNamesPacketHandler());
        registerHandler(UpdatePlayerDataPacket.class, new UpdatePlayerDataPacketHandler());
        registerHandler(RandomTPWorldsPacket.class, new RandomTPWorldsPacketHandler());
        registerHandler(TeleportCommandOptionsPacket.class, new TeleportCommandOptionsPacketHandler());
        registerHandler(TeleportBackPacket.class, new TeleportBackPacketHandler());
    }

    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel((WarpSystem) proxy, channelProxy);
        Bukkit.getMessenger().registerIncomingPluginChannel((WarpSystem) proxy, channelBackend, this);
    }

    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel((WarpSystem) proxy, channelProxy);
        Bukkit.getMessenger().unregisterIncomingPluginChannel((WarpSystem) proxy, channelBackend, this);
    }

    @Override
    protected void send(byte[] data, Player p) {
        if (p == null) p = getAny();
        if (p == null) return; //nobody online

        p.sendPluginMessage(getProxy(), channelProxy, data);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String tag, @NotNull Player player, @NotNull byte[] bytes) {
        if (tag.equals(getChannelBackend())) receive(bytes, player);
    }

    public void send(@NotNull Packet packet) {
        super.send(packet, null);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet) {
        return super.send(packet, null);
    }

    private Player getAny() {
        Optional<? extends Player> opt = Bukkit.getOnlinePlayers().stream().findFirst();
        return opt.orElse(null);
    }
}
