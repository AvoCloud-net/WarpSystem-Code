package de.codingair.warpsystem.spigot.features.playerwarps.listeners;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.warpsystem.base.transfer.packets.proxy.SendPlayerWarpOptionsPacket;
import de.codingair.warpsystem.base.transfer.packets.general.DeletePlayerWarpPacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpUpdatePacket;
import de.codingair.warpsystem.base.transfer.packets.general.SendPlayerWarpsPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PlayerWarpTeleportProcessPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.PlayerWarpUpdate;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class PlayerWarpListener implements Listener {

    public PlayerWarpListener() {
        //register Packets
        WarpSystem.getDataHandler().registerHandler(SendPlayerWarpsPacket.class, (packet, proxy, connection, direction) -> {
            List<PlayerWarpData> l = packet.getData();

            for(PlayerWarpData s : l) {
                PlayerWarp w = new PlayerWarp();
                w.setData(s);
                PlayerWarpManager.getManager().updateWarp(w);
                s.destroy();
            }

            l.clear();
            PlayerWarpManager.getManager().updateGUIs();
        });

        WarpSystem.getDataHandler().registerHandler(SendPlayerWarpUpdatePacket.class, (packet, proxy, connection, direction) -> {
            PlayerWarpUpdate update = packet.getUpdate();

            PlayerWarp w = PlayerWarpManager.getManager().getWarp(update.getId(), update.getOriginName());
            w.setData(update);
            update.destroy();
            PlayerWarpManager.getManager().updateGUIs();
        });

        WarpSystem.getDataHandler().registerHandler(SendPlayerWarpOptionsPacket.class, (packet, proxy, connection, direction) -> PlayerWarpManager.getManager().setInactiveTime(packet.getInactiveTime()));

        WarpSystem.getDataHandler().registerHandler(DeletePlayerWarpPacket.class, (packet, proxy, connection, direction) -> {
            PlayerWarp warp = PlayerWarpManager.getManager().getWarp(packet.getId(), packet.getName());
            PlayerWarpManager.getManager().delete(warp, false);
            if(warp != null) warp.setSource(true);
            PlayerWarpManager.getManager().updateGUIs();
        });

        WarpSystem.getDataHandler().registerHandler(PlayerWarpTeleportProcessPacket.class, (packet, proxy, connection, direction) -> {
            PlayerWarp warp = PlayerWarpManager.getManager().getWarp(packet.getId(), packet.getName());
            if(warp != null) {
                if(packet.increaseSales()) warp.increaseInactiveSales();
                if(packet.resetSales()) warp.resetInactiveSales();
                if(packet.increasePerformed()) warp.increasePerformed();

                PlayerWarpManager.getManager().updateGUIs();
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerFinalJoinEvent e) {
        PlayerWarpManager.getManager().checkPlayerWarpOwnerNames(e.getPlayer());
        List<PlayerWarp> notify = new ArrayList<>();

        boolean timeDependent = PlayerWarpManager.getManager().isEconomy();
        double money = 0;
        List<PlayerWarp> warps = PlayerWarpManager.getManager().getOwnWarps(e.getPlayer());
        for(PlayerWarp warp : warps) {
            if(timeDependent && warp.isExpired()) {
                notify.add(warp);
            }

            money += warp.getInactiveSales() * warp.getTeleportCosts();
        }

        if(money > 0 || !notify.isEmpty()) {
            double finalMoney = money;
            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                if(!notify.isEmpty()) {
                    for(PlayerWarp warp : notify) {
                        e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Warp_expiring").replace("%NAME%", warp.getName()).replace("%TIME_LEFT%", StringFormatter.convertInTimeFormat(PlayerWarpManager.getManager().getInactiveTime() - (System.currentTimeMillis() - warp.getExpireDate()), 0, "", "")));
                    }
                    notify.clear();
                }

                if(finalMoney > 0) {
                    SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("Warp_Money_Available").replace("%AMOUNT%", new ImprovedDouble(finalMoney).toString()), WarpSystem.getInstance());
                    message.replace("%BUTTON%", new ChatButton(Lang.get("Warp_Money_Available_Button")) {
                        @Override
                        public void onClick(Player player) {
                            new PWList(player).open();
                            message.destroy();
                        }
                    }.setHover(Lang.get("Click_Hover")));

                    message.setTimeOut(60);

                    message.send(e.getPlayer());
                }
            }, 5 * 20L);
        }
    }
}
