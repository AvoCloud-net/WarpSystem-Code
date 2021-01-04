package de.codingair.warpsystem.bungee.transfer.bungee;

import de.codingair.codingapi.bungeecord.BungeeAPI;
import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.handlers.CooldownDataPacketHandler;
import de.codingair.warpsystem.base.transfer.handlers.CooldownPacketHandler;
import de.codingair.warpsystem.base.transfer.packets.bungee.SendJarPacket;
import de.codingair.warpsystem.base.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.*;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.transfer.handlers.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class BungeeHandler extends DataHandler<ServerInfo> implements Listener {
    public BungeeHandler(WarpSystem plugin) {
        super("warpsystem", plugin);
    }

    @Override
    public void registering() {
        for(PacketType value : PacketType.values()) {
            registerPacket(value.getPacket());
        }

        registerHandler(SendPlayerWarpsPacket.class, new SendPlayerWarpsPacketHandler());
        registerHandler(RegisterServerForPlayerWarpsPacket.class, new RegisterServerForPlayerWarpsPacketHandler());
        registerHandler(MoveLocalPlayerWarpsPacket.class, new MoveLocalPlayerWarpsPacketHandler());
        registerHandler(SendPlayerWarpUpdatePacket.class, new SendPlayerWarpUpdatePacketHandler());
        registerHandler(PlayerWarpTeleportProcessPacket.class, new PlayerWarpTeleportProcessPacketHandler());
        registerHandler(DeletePlayerWarpPacket.class, new DeletePlayerWarpPacketHandler());
        registerHandler(TeleportCommandOptionsPacket.class, new TeleportCommandOptionsPacketHandler());
        registerHandler(TeleportRequestHandledPacket.class, new TeleportRequestHandledPacketHandler());
        registerHandler(PrepareTeleportPlayerToPlayerPacket.class, new PrepareTeleportPlayerToPlayerPacketHandler());
        registerHandler(PrepareTeleportRequestPacket.class, new PrepareTeleportRequestPacketHandler());
        registerHandler(PrepareTeleportPacket.class, new PrepareTeleportPacketHandler());
        registerHandler(ToggleForceTeleportsPacket.class, new ToggleForceTeleportsPacketHandler());
        registerHandler(PublishGlobalWarpPacket.class, new PublishGlobalWarpPacketHandler());
        registerHandler(DeleteGlobalWarpPacket.class, new DeleteGlobalWarpPacketHandler());
        registerHandler(GlobalWarpTeleportPacket.class, new GlobalWarpTeleportPacketHandler());
        registerHandler(RequestGlobalWarpNamesPacket.class, new RequestGlobalWarpNamesPacketHandler());
        registerHandler(SendOptionsPacket.class, new SendOptionsPacketHandler());
        registerHandler(TeleportCommandOptionsPacket.class, new TeleportCommandOptionsPacketHandler());
        registerHandler(TeleportRequestHandledPacket.class, new TeleportRequestHandledPacketHandler());
        registerHandler(PrepareTeleportPlayerToPlayerPacket.class, new PrepareTeleportPlayerToPlayerPacketHandler());
        registerHandler(PrepareTeleportRequestPacket.class, new PrepareTeleportRequestPacketHandler());
        registerHandler(PrepareTeleportPacket.class, new PrepareTeleportPacketHandler());
        registerHandler(ToggleForceTeleportsPacket.class, new ToggleForceTeleportsPacketHandler());
        registerHandler(PrepareCoordinationTeleportPacket.class, new PrepareCoordinationTeleportPacketHandler());
        registerHandler(RequestUUIDPacket.class, new SendUUIDPacketHandler());
        registerHandler(MessagePacket.class, new MessagePacketHandler());
        registerHandler(PrepareServerSwitchPacket.class, new PrepareServerSwitchPacketHandler());
        registerHandler(RequestFullNamePacket.class, new RequestFullNamePacketHandler());
        registerHandler(RequestInitialPacket.class, new RequestInitialPacketHandler());
        registerHandler(RequestServerStatusPacket.class, new RequestServerStatusPacketHandler());
    }

    public void onEnable() {
        BungeeAPI.getProxy().getPluginManager().registerListener((WarpSystem) proxy, this);
        BungeeAPI.getProxy().registerChannel(channelProxy);
        BungeeAPI.getProxy().registerChannel(channelBackend);
    }

    public void onDisable() {
        BungeeAPI.getProxy().getPluginManager().unregisterListener(this);
        BungeeAPI.getProxy().unregisterChannel(channelProxy);
        BungeeAPI.getProxy().unregisterChannel(channelBackend);
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    protected void send(byte[] data, ServerInfo connection, Direction direction) {
        if(direction == Direction.DOWN) connection.sendData(channelBackend, data);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if(e.getTag().equals(getChannelProxy())) {
            receive(e.getData(), ((ProxiedPlayer) e.getReceiver()).getServer().getInfo(), Direction.DOWN);
        }
    }

    @Override
    public <P extends Packet> boolean registerHandler(@NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        return super.registerHandler(receiving, handler);
    }
}
