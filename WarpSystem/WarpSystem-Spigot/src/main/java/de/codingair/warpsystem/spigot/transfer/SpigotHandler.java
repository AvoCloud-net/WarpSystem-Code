package de.codingair.warpsystem.spigot.transfer;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.ResponsePacket;
import de.codingair.packetmanagement.variants.bytestream.OneWayStreamDataHandler;
import de.codingair.warpsystem.core.transfer.packets.general.*;
import de.codingair.warpsystem.core.transfer.packets.proxy.*;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.TeleportRequestHandledPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ConnectionPacket;
import de.codingair.warpsystem.core.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.transfer.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpigotHandler extends OneWayStreamDataHandler<Player> implements PluginMessageListener {
    public SpigotHandler(WarpSystem plugin) {
        super("warpsystem", plugin);

        setIgnoreUnregistered(true);
    }

    @Override
    public void registering() {
        for (PacketType value : PacketType.values()) {
            registerPacket(value.getPacket());
        }

        if (!WarpSystem.getInstance().isUseProxy()) {
            // fix: custom payload attacks
            // When WarpSystem is not enabled on the proxy,
            // all packets must be ignored to prevent any communication with external clients.
            return;
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
        registerHandler(ProvidePlayerDataPacket.class, new ProvidePlayerDataPacketHandler());
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
    public void onPluginMessageReceived(@NotNull String tag, @NotNull Player player, byte[] bytes) {
        if (tag.equals(getChannelBackend())) receive(bytes, player);
    }

    private Player getAny() {
        Optional<? extends Player> opt = Bukkit.getOnlinePlayers().stream().findFirst();
        return opt.orElse(null);
    }

    @Override
    public <P extends Packet> boolean registerHandler(@NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        if (!WarpSystem.getInstance().isUseProxy()) {
            // fix: custom payload attacks
            // When WarpSystem is not enabled on the proxy,
            // all packets must be ignored to prevent any communication with external clients.
            return false;
        }
        return super.registerHandler(receiving, handler);
    }

    @Override
    public void send(@NotNull Packet packet, @Nullable Player connection) {
        if (!WarpSystem.getInstance().isUseProxy()) return;
        super.send(packet, connection);
    }

    @Override
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable Player connection) {
        if (!WarpSystem.getInstance().isUseProxy()) {
            CompletableFuture<A> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("TradeProxy not connected."));
            return future;
        }
        return super.send(packet, connection);
    }

    @Override
    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable Player connection, long timeOut) {
        if (!WarpSystem.getInstance().isUseProxy() && !(packet instanceof ConnectionPacket)) {
            CompletableFuture<A> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("TradeProxy not connected."));
            return future;
        }
        return super.send(packet, connection, timeOut);
    }
}
