package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.MoveLocalPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.playerwarps.managers.PlayerWarpManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoveLocalPlayerWarpsPacketHandler implements PacketHandler<MoveLocalPlayerWarpsPacket> {
    @Override
    public void process(@NotNull MoveLocalPlayerWarpsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        List<List<PlayerWarpData>> uploads = new ArrayList<>();
        List<PlayerWarpData> l = new ArrayList<>();

        for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
            for(PlayerWarpData w : value) {
                if(w.getServer().equalsIgnoreCase(((ServerInfo) connection).getName())) l.add(w);
                if(l.size() == 100) {
                    uploads.add(new ArrayList<>(l));
                    l.clear();
                }
            }
        }

        if(!l.isEmpty()) uploads.add(l);

        PlayerWarpManager.getInstance().setActive((ServerInfo) connection, false);

        for(List<PlayerWarpData> upload : uploads) {
            for(PlayerWarpData d : upload) {
                PlayerWarpManager.getInstance().delete(d, true);
            }

            SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
            WarpSystem.getDataHandler().send(p, (ServerInfo) connection, Direction.DOWN);
        }

        uploads.clear();
    }
}
