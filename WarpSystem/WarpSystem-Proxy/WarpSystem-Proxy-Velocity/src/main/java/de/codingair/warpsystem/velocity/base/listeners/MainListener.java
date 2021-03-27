package de.codingair.warpsystem.velocity.base.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.codingair.warpsystem.core.proxy.base.Permissions;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.base.events.ServerInitializeEvent;
import de.codingair.warpsystem.velocity.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.velocity.base.utils.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainListener {
    private final Set<RegisteredServer> asking = new HashSet<>();

    @Subscribe
    public void onConnect(ServerConnectedEvent e) {
        if (asking.contains(e.getServer())) {
            ask(e.getPlayer());
        }
    }

    private void ask(Player player) {
        if (player.hasPermission(Permissions.PERMISSION_MODIFY_SYSTEM)) {
            WarpSystem.getInstance().schedule(() -> {
                Component base = Component.text(Lang.getPrefix() + "§7Do you want to §cfetch §7a new update from your §cBungeeCord§7? §8[");

                Component extra = Component.text("§aYes");
                extra.hoverEvent(Component.text(Lang.get("Click_Hover")));
                extra.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/warpsystembungee fetch"));

                base.append(extra);
                base.append(Component.text("§8]"));

                player.sendMessage(base);
            }, 4, 0, TimeUnit.SECONDS);
        }
    }

    @Subscribe
    public void onInit(ServerInitializeEvent e) {
        asking.remove(e.getServer().getServer());
    }

    @Subscribe
    public void onOptions(ServerProvideOptionsEvent e) {
        int i = e.getOptions().getUpdateFetching();
        if (i == 1) {
            //ask
            if (WarpSystem.getInstance().getJarManager().fetchPossible(e.getServer())) {
                asking.add(e.getServer().getServer());

                for (Player player : e.getServer().getServer().getPlayersConnected()) {
                    ask(player);
                }
            }
        } else if (i == 2) {
            if (WarpSystem.getInstance().getJarManager().fetchPossible(e.getServer())) WarpSystem.getInstance().getJarManager().sendJar(e.getServer(), null);
        }
    }
}
