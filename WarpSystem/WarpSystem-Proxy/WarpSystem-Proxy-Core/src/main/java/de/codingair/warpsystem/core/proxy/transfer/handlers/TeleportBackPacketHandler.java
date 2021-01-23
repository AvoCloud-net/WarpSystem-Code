package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.BytePacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportBackPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TeleportBackPacketHandler implements ResponsibleMultiLayerPacketHandler<TeleportBackPacket, BytePacket> {

    @Override
    public boolean answer(@NotNull TeleportBackPacket packet, @NotNull Proxy proxy, @NotNull Direction direction) {
        //redis
        //we might not be able to handle this packet!
        return direction == Direction.DOWN || Players.getPlayer(packet.getName()) != null;
    }

    @Override
    public @NotNull CompletableFuture<BytePacket> response(@NotNull TeleportBackPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Players.getPlayer(packet.getName());

        if (player == null) throw new Escalation(this, Direction.UP, packet, err -> new BytePacket(TeleportBackPacket.Result.PLAYER_NOT_AVAILABLE.id()));
        else {
            if(packet.isSwitching()) {
                PlayerData data = Core.getPlugin().getPlayerData().getCacheExact(packet.getName());

                String to = data.getOldServer();
                if(to == null) return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.SERVER_NOT_AVAILABLE.id()));

                Server<?> server = Core.getPlugin().getServer(to);
                if(server == null) return CompletableFuture.completedFuture(new BytePacket(TeleportBackPacket.Result.SERVER_NOT_AVAILABLE.id()));

                CompletableFuture<BytePacket> future = new CompletableFuture<>();
                ServerHandler.sendPlayerTo(server, player, new Callback<Server<?>>() {
                    @Override
                    public void accept(Server<?> server) {
                        Core.getPlugin().dataHandler().send(packet, player.getServer(), Direction.DOWN).whenComplete((p, t) -> {
                            if(t != null) future.completeExceptionally(t);
                            else future.complete(p);
                        });
                    }
                });

                return future;
            } else return Core.getPlugin().dataHandler().send(packet, player.getServer(), Direction.DOWN);
        }
    }
}
