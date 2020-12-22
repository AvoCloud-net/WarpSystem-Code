package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.bungee.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.RegisterServerForPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.managers.PlayerWarpManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegisterServerForPlayerWarpsPacketHandler implements PacketHandler<RegisterServerForPlayerWarpsPacket> {
    @Override
    public void process(@NotNull RegisterServerForPlayerWarpsPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        List<List<PlayerWarpData>> uploads = new ArrayList<>();
        List<PlayerWarpData> l = new ArrayList<>();

        for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
            for(PlayerWarpData w : value) {
                l.add(w);

                if(l.size() == 100) {
                    uploads.add(new ArrayList<>(l));
                    l.clear();
                }
            }
        }

        if(!l.isEmpty()) uploads.add(l);

        PlayerWarpManager.getInstance().setActive((ServerInfo) connection, true);
        PlayerWarpManager.getInstance().setTimeDependent((ServerInfo) connection, packet.isTimeDependent());

        SendPlayerWarpOptionsPacket options = new SendPlayerWarpOptionsPacket(PlayerWarpManager.getInstance().getInactiveTime());
        WarpSystem.getDataHandler().send(options, (ServerInfo) connection, Direction.DOWN);

        for(List<PlayerWarpData> upload : uploads) {
            SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
            WarpSystem.getDataHandler().send(p, (ServerInfo) connection, Direction.DOWN);
        }

        uploads.clear();
    }
}
