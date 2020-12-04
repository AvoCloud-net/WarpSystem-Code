package de.codingair.warpsystem.velocity.features.playerwarps.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.codingair.warpsystem.base.transfer.packets.bungee.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.base.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PlayerWarpTeleportProcessPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.RegisterServerForPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.base.transfer.packets.utils.Packet;
import de.codingair.warpsystem.base.transfer.packets.utils.PacketType;
import de.codingair.warpsystem.base.transfer.serializeable.Serializable;
import de.codingair.warpsystem.base.transfer.utils.PacketListener;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.features.playerwarps.managers.PlayerWarpManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerWarpListener extends PacketListener {

    @Subscribe
    public void onJoin(ServerPreConnectEvent e) {
        PlayerWarpManager.getInstance().checkPlayerWarpOwnerNames(e.getPlayer());
    }

    @Override
    public void onReceive(Packet packet, String extra) {
        if(packet.getType() == PacketType.SendPlayerWarpsPacket) {
            List<PlayerWarpData> l = ((SendPlayerWarpsPacket) packet).getData();

            for(Serializable s : l) {
                PlayerWarpData w = (PlayerWarpData) s;
                PlayerWarpManager.getInstance().updateWarp(w);
            }

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getServerInfo().getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
        } else if(packet.getType() == PacketType.RegisterServerForPlayerWarpsPacket) {
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

            Optional<RegisteredServer> oS = WarpSystem.proxy().getServer(extra);
            if(!oS.isPresent()) return;
            RegisteredServer server = oS.get();

            PlayerWarpManager.getInstance().setActive(server, true);
            PlayerWarpManager.getInstance().setTimeDependent(server, ((RegisterServerForPlayerWarpsPacket) packet).isTimeDependent());

            SendPlayerWarpOptionsPacket options = new SendPlayerWarpOptionsPacket(PlayerWarpManager.getInstance().getInactiveTime());
            WarpSystem.getInstance().getDataHandler().send(options, server);

            for(List<PlayerWarpData> upload : uploads) {
                SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
                WarpSystem.getInstance().getDataHandler().send(p, server);
            }

            uploads.clear();
        } else if(packet.getType() == PacketType.MoveLocalPlayerWarpsPacket) {
            List<List<PlayerWarpData>> uploads = new ArrayList<>();
            List<PlayerWarpData> l = new ArrayList<>();

            for(List<PlayerWarpData> value : PlayerWarpManager.getInstance().getWarps().values()) {
                for(PlayerWarpData w : value) {
                    if(w.getServer().equalsIgnoreCase(extra)) l.add(w);
                    if(l.size() == 100) {
                        uploads.add(new ArrayList<>(l));
                        l.clear();
                    }
                }
            }

            if(!l.isEmpty()) uploads.add(l);

            Optional<RegisteredServer> oS = WarpSystem.proxy().getServer(extra);
            if(!oS.isPresent()) return;
            RegisteredServer server = oS.get();
            PlayerWarpManager.getInstance().setActive(server, false);

            for(List<PlayerWarpData> upload : uploads) {
                for(PlayerWarpData d : upload) {
                    PlayerWarpManager.getInstance().delete(d, true);
                }

                SendPlayerWarpsPacket p = new SendPlayerWarpsPacket(upload);
                WarpSystem.getInstance().getDataHandler().send(p, server);
            }

            uploads.clear();
        } else if(packet.getType() == PacketType.SendPlayerWarpUpdatesPacket) {
            PlayerWarpUpdate update = ((SendPlayerWarpUpdatePacket) packet).getUpdate();

            PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(update.getId(), update.getOriginName());
            w.apply(update);

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getServerInfo().getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
            update.destroy();
        } else if(packet.getType() == PacketType.PlayerWarpTeleportProcessPacket) {
            PlayerWarpTeleportProcessPacket p = (PlayerWarpTeleportProcessPacket) packet;

            PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(p.getId(), p.getName());

            if(p.increaseSales()) w.increaseInactiveSales();
            if(p.resetSales()) w.setInactiveSales((byte) 0);
            if(p.increasePerformed()) w.increasePerformed();

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getServerInfo().getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
        } else if(packet.getType() == PacketType.DeletePlayerWarpPacket) {
            DeletePlayerWarpPacket p = (DeletePlayerWarpPacket) packet;

            PlayerWarpData w = PlayerWarpManager.getInstance().getWarp(p.getId(), p.getName());

            PlayerWarpManager.getInstance().delete(w, false);

            //forwarding
            PlayerWarpManager.getInstance().interactWithServers(s -> {
                if(s.getServerInfo().getName().equalsIgnoreCase(extra)) return;
                WarpSystem.getInstance().getDataHandler().send(packet, s);
            });
        }
    }

    @Override
    public boolean onSend(Packet packet) {
        return false;
    }
}
