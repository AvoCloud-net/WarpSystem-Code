package de.codingair.warpsystem.bungee.base.listeners;

import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.Lang;
import de.codingair.warpsystem.bungee.base.utils.ServerInitializeEvent;
import de.codingair.warpsystem.bungee.base.utils.ServerProvideOptionsEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainListener implements Listener {
    private final Set<ServerInfo> asking = new HashSet<>();

    @EventHandler
    public void onConnect(ServerConnectedEvent e) {
        if(e.getServer().getInfo().getPlayers().size() == 0) {
            //Update it
            WarpSystem.getInstance().getServerManager().sendInitialPacket(e.getServer().getInfo());
        }

        if(asking.contains(e.getServer().getInfo())) {
            ask(e.getPlayer());
        }
    }

    private void ask(ProxiedPlayer player) {
        if(player.hasPermission(WarpSystem.PERMISSION_MODIFY_SYSTEM)) {
            WarpSystem.getInstance().getProxy().getScheduler().schedule(WarpSystem.getInstance(), () -> {
                TextComponent base = new TextComponent(Lang.getPrefix() + "§7Do you want to §cfetch §7a new update from your §cBungeeCord§7? §8[");

                TextComponent extra = new TextComponent("§aYes");
                extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(Lang.get("Click_Hover"))}));
                extra.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warpsystembungee fetch"));

                base.addExtra(extra);
                base.addExtra("§8]");

                player.sendMessage(base);
            }, 4, TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void onInit(ServerInitializeEvent e) {
        asking.remove(e.getInfo());
    }

    @EventHandler
    public void onOptions(ServerProvideOptionsEvent e) {
        int i = e.getOptions().getUpdateFetching();
        if(i == 1) {
            //ask
            if(WarpSystem.getInstance().getJarManager().fetchPossible(e.getInfo())) {
                asking.add(e.getInfo());

                for(ProxiedPlayer player : e.getInfo().getPlayers()) {
                    ask(player);
                }
            }
        } else if(i == 2) {
            if(WarpSystem.getInstance().getJarManager().fetchPossible(e.getInfo())) WarpSystem.getInstance().getJarManager().sendJar(e.getInfo(), null);
        }
    }
}
