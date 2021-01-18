package de.codingair.warpsystem.core.transfer.packets.utils;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.warpsystem.core.transfer.packets.general.*;
import de.codingair.warpsystem.core.transfer.packets.proxy.*;
import de.codingair.warpsystem.core.transfer.packets.spigot.*;

public enum PacketType {
    InitialPacket(de.codingair.warpsystem.core.transfer.packets.proxy.InitialPacket.class),
    RequestInitialPacket(de.codingair.warpsystem.core.transfer.packets.spigot.RequestInitialPacket.class),
    RequestServerStatusPacket(de.codingair.warpsystem.core.transfer.packets.spigot.RequestServerStatusPacket.class),
    ChatInputGUITogglePacket(de.codingair.warpsystem.core.transfer.packets.spigot.ChatInputGUITogglePacket.class),
    SendGlobalSpawnOptionsPacket(de.codingair.warpsystem.core.transfer.packets.general.SendGlobalSpawnOptionsPacket.class),
    TeleportSpawnPacket(de.codingair.warpsystem.core.transfer.packets.general.TeleportSpawnPacket.class),
    SendJarPacket(SendJarPacket.class),
    SendOptionsPacket(de.codingair.warpsystem.core.transfer.packets.spigot.SendOptionsPacket.class),

    PublishGlobalWarpPacket(de.codingair.warpsystem.core.transfer.packets.spigot.PublishGlobalWarpPacket.class),
    GlobalWarpTeleportPacket(GlobalWarpTeleportPacket.class),
    DeleteGlobalWarpPacket(DeleteGlobalWarpPacket.class),
    RequestGlobalWarpNamesPacket(RequestGlobalWarpNamesPacket.class),
    SendGlobalWarpNamesPacket(de.codingair.warpsystem.core.transfer.packets.proxy.SendGlobalWarpNamesPacket.class),
    UpdateGlobalWarpPacket(de.codingair.warpsystem.core.transfer.packets.proxy.UpdateGlobalWarpPacket.class),
    PerformCommandOnSpigotPacket(de.codingair.warpsystem.core.transfer.packets.proxy.PerformCommandOnSpigotPacket.class),
    PerformCommandOnBungeePacket(PerformCommandOnBungeePacket.class),
    TeleportPlayerToPlayerPacket(TeleportPlayerToPlayerPacket.class),
    TeleportPlayerToCoordsPacket(TeleportPlayerToCoordsPacket.class),
    PrepareServerSwitchPacket(PrepareServerSwitchPacket.class),
    PrepareLoginMessagePacket(PrepareLoginMessagePacket.class),
    MessagePacket(MessagePacket.class),
    CooldownPacket(CooldownPacket.class),
    CooldownDataPacket(CooldownDataPacket.class),

    TeleportCommandOptions(TeleportCommandOptionsPacket.class),
    TeleportRequestHandledPacket(TeleportRequestHandledPacket.class),
    PrepareTeleportPlayerToPlayerPacket(PrepareTeleportPlayerToPlayerPacket.class),
    PrepareTeleportRequestPacket(PrepareTeleportRequestPacket.class),
    StartTeleportToPlayerPacket(de.codingair.warpsystem.core.transfer.packets.general.StartTeleportToPlayerPacket.class),
    ToggleForceTeleportsPacket(ToggleForceTeleportsPacket.class),
    PrepareTeleportPacket(PrepareTeleportPacket.class),

    SendPlayerWarpsPacket(de.codingair.warpsystem.core.transfer.packets.general.SendPlayerWarpsPacket.class),
    RegisterServerForPlayerWarpsPacket(RegisterServerForPlayerWarpsPacket.class),
    MoveLocalPlayerWarpsPacket(MoveLocalPlayerWarpsPacket.class),
    SendPlayerWarpUpdatesPacket(SendPlayerWarpUpdatePacket.class),
    PrepareCoordinationTeleportPacket(PrepareCoordinationTeleportPacket.class),
    SendPlayerWarpOptionsPacket(SendPlayerWarpOptionsPacket.class),
    DeletePlayerWarpPacket(DeletePlayerWarpPacket.class),
    PlayerWarpTeleportProcessPacket(PlayerWarpTeleportProcessPacket.class),

    RandomTPPacket(RandomTPPacket.class),
    RandomTPWorldsPacket(RandomTPWorldsPacket.class),
    QueueRTPUsagePacket(QueueRTPUsagePacket.class),
    ToggleSetupAssistantPacket(ToggleSetupAssistantPacket.class),
    SetupAssistantStorePacket(SetupAssistantStorePacket.class),

    RequestFullNamePacket(RequestFullNamePacket.class),

    SendServerPropertiesPacket(SendServerPropertiesPacket.class),
    SendUUIDPacket(SendUUIDPacket.class),
    RequestUUIDPacket(RequestUUIDPacket.class),
    ProvidePlayerDataPacket(ProvidePlayerDataPacket.class),
    PlayerJoinPacket(PlayerJoinPacket.class),
    PlayerQuitPacket(PlayerQuitPacket.class),
    UpdatePlayerDataPacket(UpdatePlayerDataPacket.class),
    TeleportBackPacket(TeleportBackPacket.class),
    ;

    private final Class<? extends Packet> packet;

    PacketType(Class<? extends Packet> packet) {
        this.packet = packet;
    }

    public static PacketType getById(int id) {
        for (PacketType packetType : values()) {
            if (packetType.getId() == id) return packetType;
        }

        return null;
    }

    public static PacketType getByObject(Object packet) {
        if (packet == null) return null;

        for (PacketType packetType : values()) {
            if (packetType.getPacket().equals(packet.getClass())) return packetType;
        }

        return null;
    }

    public int getId() {
        return ordinal();
    }

    public Class<? extends Packet> getPacket() {
        return packet;
    }
}
