package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportBackPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.managers.TeleportManager;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommandManager extends de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager {

    @Override
    public Location invalidateBackPosition(Player player) {
        return this.backPosition.remove(player.getName());
    }

    @Override
    public boolean teleportToLastBackLocation(Player player, boolean proxy) {
        Location l;

        if (proxy) {
            l = this.quitPosition.remove(player.getName());
            if (l == null) return false;
        } else {
            l = this.backPosition.remove(player.getName());
            if (l == null) {
                PlayerData data = WarpSystem.getInstance().getPlayerDataManager().getCache(player);
                if (data.getOldServer() != null) {
                    //switch server
                    TeleportOptions options = new TeleportOptions(new Destination(new EmptyAdapter()), Lang.get("Last_Position"));
                    options.setMessage(null);

                    options.addCallback(new Callback<Result>() {
                        @Override
                        public void accept(Result result) {
                            if (result == Result.SUCCESS) {
                                WarpSystem.getDataHandler().send(new TeleportBackPacket(player.getName(), false, true), player).whenComplete((success, t) -> {
                                    TeleportBackPacket.Result res = TeleportBackPacket.Result.fromId(success.getByte());
                                    if (res == null) return;

                                    switch (res) {
                                        case SUCCESS:
                                            break;

                                        case SERVER_NOT_AVAILABLE:
                                            player.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
                                            break;

                                        case PLAYER_NOT_AVAILABLE:
                                            player.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                                            break;

                                        case NO_LAST_POSITION:
                                            player.sendMessage(Lang.getPrefix() + Lang.get("No_last_position_found"));
                                            break;
                                    }
                                });
                            }
                        }
                    });

                    TeleportManager.getInstance().teleport(player, options);
                    return true;
                } else return false;
            }
        }

        teleportBack(player, l);
        return true;
    }
}
