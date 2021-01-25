package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportRequestPacketHandler implements ResponsiblePacketHandler<PrepareTeleportRequestPacket, LongPacket> {
    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportRequestPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        CompletableFuture<LongPacket> future = new CompletableFuture<>();

        if(TeleportCommandManager.getInstance() != null) {
            TeleportCommandManager.getInstance().invite(packet.getSender(), packet.isTpToSender(), new Callback<Long>() {
                @Override
                public void accept(Long result) {
                    future.complete(new LongPacket(result));
                }
            }, packet.getRecipient(), true);
        } else future.complete(new LongPacket(0));

        return future;
    }
}
