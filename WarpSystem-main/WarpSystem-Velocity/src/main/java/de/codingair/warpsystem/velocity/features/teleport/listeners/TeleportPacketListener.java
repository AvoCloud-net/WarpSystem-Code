package de.codingair.warpsystem.velocity.features.teleport.listeners;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.base.transfer.packets.bungee.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.base.transfer.packets.bungee.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.base.transfer.packets.general.IntegerPacket;
import de.codingair.warpsystem.base.transfer.packets.general.LongPacket;
import de.codingair.warpsystem.base.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.*;
import de.codingair.warpsystem.base.transfer.packets.utils.Packet;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.base.transfer.utils.PacketListener;
import de.codingair.warpsystem.velocity.api.Players;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.features.teleport.managers.TeleportManager;

import java.util.Optional;

public class TeleportPacketListener extends PacketListener {
    @Override
    public void onReceive(Packet packet, String extra) {
        Optional<RegisteredServer> oS = WarpSystem.proxy().getServer(extra);
        if(!oS.isPresent()) return;
        RegisteredServer source = oS.get();
        
        if(PacketType.getByObject(packet) == PacketType.TeleportCommandOptions) {
            Optional<RegisteredServer> target = WarpSystem.proxy().getServer(extra);
            TeleportCommandOptionsPacket p = (TeleportCommandOptionsPacket) packet;

            target.ifPresent(registeredServer -> TeleportManager.getInstance().registerOptions(registeredServer, p.getOptions()));
        } else if(PacketType.getByObject(packet) == PacketType.TeleportRequestHandledPacket) {
            Player sender = Players.getPlayer(((TeleportRequestHandledPacket) packet).getSender());
            if(sender != null && sender.getCurrentServer().isPresent()) WarpSystem.getInstance().getDataHandler().send(packet, sender.getCurrentServer().get().getServer());
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportPlayerToPlayerPacket) {
            PrepareTeleportPlayerToPlayerPacket p = (PrepareTeleportPlayerToPlayerPacket) packet;

            Player player = Players.getPlayer(p.getPlayer());
            Player target = Players.getPlayer(p.getDestinationPlayer());

            Optional<ServerConnection> oCS = player.getCurrentServer();
            Optional<ServerConnection> oTS = target.getCurrentServer();

            IntegerPacket answer = new IntegerPacket(0);
            p.applyAsAnswer(answer);

            if(player == null || target == null || !oCS.isPresent() || !oTS.isPresent()) {
                answer.setValue(1);
                WarpSystem.getInstance().getDataHandler().send(answer, source);
            } else {
                RegisteredServer currentServer = oCS.get().getServer();
                RegisteredServer targetServer = oTS.get().getServer();

                TeleportPlayerToPlayerPacket tpPacket = new TeleportPlayerToPlayerPacket(player.getUsername(), player.getUsername(), target.getUsername());
                tpPacket.setCosts(p.getCosts());
                WarpSystem.getInstance().getDataHandler().send(tpPacket, targetServer);

                if(!currentServer.equals(targetServer)) {
                    player.createConnectionRequest(targetServer).connect().whenComplete((result, error) -> {
                        if(!result.isSuccessful()) answer.setValue(2);
                        WarpSystem.getInstance().getDataHandler().send(answer, source);
                    });
                } else WarpSystem.getInstance().getDataHandler().send(answer, source);
            }

        } else if(PacketType.getByObject(packet) == PacketType.StartTeleportToPlayerPacket) {
            StartTeleportToPlayerPacket tpPacket = (StartTeleportToPlayerPacket) packet;
            Player player = Players.getPlayer(tpPacket.getPlayer());
            Optional<ServerConnection> con = player.getCurrentServer();
            if(!con.isPresent()) return;

            IntegerPacket answer = new IntegerPacket(0);
            tpPacket.applyAsAnswer(answer);

            if(player != null) WarpSystem.getInstance().getDataHandler().send(new StartTeleportToPlayerPacket(new Callback<Integer>() {
                @Override
                public void accept(Integer id) {
                    answer.setValue(id);
                    WarpSystem.getInstance().getDataHandler().send(answer, source);
                }
            }, tpPacket.getPlayer(), tpPacket.getTo(), tpPacket.getToDisplayName(), tpPacket.getTeleportRequestSender()), con.get().getServer());
            else {
                answer.setValue(1);
                WarpSystem.getInstance().getDataHandler().send(answer, source);
            }
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportRequestPacket) {
            PrepareTeleportRequestPacket tpPacket = (PrepareTeleportRequestPacket) packet;

            String recipient = tpPacket.getRecipient();

            if(recipient == null) {
                //forward to all
                int servers = WarpSystem.getInstance().getServerManager().getOnlineServer().size() - 1;
                Value<Integer> handled = new Value<>(0);
                Value<Long> generalResult = new Value<>(0L);

                for(RegisteredServer s : WarpSystem.getInstance().getServerManager().getOnlineServer()) {
                    if(s.equals(source) || !TeleportManager.getInstance().isAccessible(s)) continue;

                    WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportRequestPacket(new Callback<Long>() {
                        @Override
                        public void accept(Long result) {
                            handled.setValue(handled.getValue() + 1);
                            generalResult.setValue(generalResult.getValue() + result);

                            if(handled.getValue() == servers) {
                                LongPacket answer = new LongPacket(generalResult.getValue());
                                tpPacket.applyAsAnswer(answer);
                                WarpSystem.getInstance().getDataHandler().send(answer, source);
                            }
                        }
                    }, tpPacket.getSender(), null, true), s);
                }
            } else {
                //only recipient
                Player player = Players.getPlayer(tpPacket.getRecipient());
                Optional<ServerConnection> con = player.getCurrentServer();
                if(!con.isPresent()) return;
                RegisteredServer server = con.get().getServer();

                if(player == null || !TeleportManager.getInstance().isAccessible(server) || WarpSystem.getVanishManager().isVanished(player.getUsername())) {
                    //not online/accessible
                    LongPacket answer = new LongPacket(0);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, source);
                } else if(TeleportManager.getInstance().deniesForceTpRequests(player)) {
                    //auto deny
                    LongPacket answer = new LongPacket(-1L << 32);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, source);
                } else {
                    WarpSystem.getInstance().getDataHandler().send(new PrepareTeleportRequestPacket(new Callback<Long>() {
                        @Override
                        public void accept(Long result) {
                            LongPacket answer = new LongPacket(result);
                            tpPacket.applyAsAnswer(answer);
                            WarpSystem.getInstance().getDataHandler().send(answer, source);
                        }
                    }, tpPacket.getSender(), player.getUsername(), tpPacket.isTpToSender()), server);
                }
            }
        } else if(PacketType.getByObject(packet) == PacketType.PrepareTeleportPacket) {
            PrepareTeleportPacket tpPacket = (PrepareTeleportPacket) packet;

            Player sender = Players.getPlayer(tpPacket.getSender());
            Player targetPlayer = tpPacket.getSender().equalsIgnoreCase(tpPacket.getTarget()) ? sender : Players.getPlayer(tpPacket.getTarget());
            Optional<ServerConnection> oSC = targetPlayer == null ? Optional.empty() : targetPlayer.getCurrentServer();

            if(targetPlayer == null || !oSC.isPresent() || !TeleportManager.getInstance().isAccessible(oSC.get().getServer())) {
                LongPacket answer = new LongPacket((((long) 0) << 32));
                tpPacket.applyAsAnswer(answer);
                WarpSystem.getInstance().getDataHandler().send(answer, source);
                return;
            }

            RegisteredServer target = oSC.get().getServer();

            String recipient = tpPacket.getRecipient();
            if(recipient == null) {
                //forward to all
                int handled = 0;
                int sent = 0;

                for(RegisteredServer s : WarpSystem.getInstance().getServerManager().getOnlineServer()) {
                    if(s.equals(source)) continue;
                    handled += s.getPlayersConnected().size();
                    if(!TeleportManager.getInstance().isAccessible(s)) continue;

                    //tp all
                    for(Player player : s.getPlayersConnected()) {
                        if(TeleportManager.getInstance().deniesForceTps(player)) continue;

                        sent++;
                        TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(tpPacket.getSender(), player.getUsername(), targetPlayer.getUsername(), false);
                        WarpSystem.getInstance().getDataHandler().send(ptpPacket, target);
                        player.createConnectionRequest(target).connect();
                    }
                }

                LongPacket answer = new LongPacket((((long) handled) << 32) | (sent & 0xffffffffL));
                tpPacket.applyAsAnswer(answer);
                WarpSystem.getInstance().getDataHandler().send(answer, source);
            } else {
                //only recipient
                Player player = Players.getPlayer(tpPacket.getRecipient());
                Optional<ServerConnection> con = player.getCurrentServer();
                if(!con.isPresent()) return;
                RegisteredServer server = con.get().getServer();

                if(player == null || !TeleportManager.getInstance().isAccessible(server)) {
                    //not online/accessible
                    LongPacket answer = new LongPacket(0);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, source);
                } else if(TeleportManager.getInstance().deniesForceTps(player) && !player.equals(sender)) {
                    //auto deny
                    LongPacket answer = new LongPacket(1L << 32);
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, source);
                } else {
                    if(tpPacket.isCoordsPacket()) {
                        TeleportPlayerToCoordsPacket ptcPacket = new TeleportPlayerToCoordsPacket(
                                tpPacket.getSender(), player.getUsername(),
                                tpPacket.getX(), tpPacket.getY(), tpPacket.getZ(),
                                false, false, false);

                        WarpSystem.getInstance().getDataHandler().send(ptcPacket, target);
                    } else {
                        TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(tpPacket.getSender(), player.getUsername(), targetPlayer.getUsername(), true);
                        WarpSystem.getInstance().getDataHandler().send(ptpPacket, target);
                    }
                    player.createConnectionRequest(target).connect();

                    LongPacket answer = new LongPacket((((long) 1) << 32) | (1 & 0xffffffffL));
                    tpPacket.applyAsAnswer(answer);
                    WarpSystem.getInstance().getDataHandler().send(answer, source);
                }
            }
        } else if(packet.getType() == PacketType.ToggleForceTeleportsPacket) {
            ToggleForceTeleportsPacket tpPacket = (ToggleForceTeleportsPacket) packet;

            Player player = Players.getPlayer(tpPacket.getPlayer());
            if(player != null) {
                TeleportManager.getInstance().setDenyForceTps(player, tpPacket.isAutoDenyTp());
                TeleportManager.getInstance().setDenyForceTpRequests(player, tpPacket.isAutoDenyTpa());
            }
        }

    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
