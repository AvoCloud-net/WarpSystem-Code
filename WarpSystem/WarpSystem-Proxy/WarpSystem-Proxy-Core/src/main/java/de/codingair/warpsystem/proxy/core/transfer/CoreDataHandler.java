package de.codingair.warpsystem.proxy.core.transfer;

import de.codingair.packetmanagement.DataHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.*;
import de.codingair.warpsystem.base.transfer.packets.spigot.*;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.proxy.core.transfer.handlers.*;
import de.codingair.warpsystem.proxy.core.utils.ProxyPlugin;
import de.codingair.warpsystem.proxy.core.utils.Server;

public abstract class CoreDataHandler extends DataHandler<Server> {
    public CoreDataHandler(ProxyPlugin plugin) {
        super("warpsystem", plugin);
    }

    @Override
    public void registering() {
        for (PacketType value : PacketType.values()) {
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
        registerHandler(UpdatePlayerDataPacket.class, new UpdatePlayerDataPacketHandler());
        registerHandler(RandomTPPacket.class, new RandomTPPacketHandler());
        registerHandler(QueueRTPUsagePacket.class, new QueueRTPUsagePacketHandler());
        registerHandler(RandomTPWorldsPacket.class, new RandomTPWorldsPacketHandler());
        registerHandler(SendGlobalSpawnOptionsPacket.class, new SendGlobalSpawnOptionsPacketHandler());
    }

    @Override
    protected boolean isConnected(Direction direction) {
        return direction == Direction.DOWN;
    }


    @Override
    protected void send(byte[] data, Server connection, Direction direction) {
        if (direction == Direction.DOWN) connection.sendData(channelBackend, data);
    }
}
