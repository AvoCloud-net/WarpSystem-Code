package de.codingair.warpsystem.velocity.base.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.codingair.warpsystem.core.transfer.packets.proxy.ToggleSetupAssistantPacket;
import de.codingair.warpsystem.velocity.base.WarpSystem;

public class SetupAssistantListener {
    private Player editing = null;
    private String message = null;

    public SetupAssistantListener() {
        WarpSystem.getInstance().getDataHandler().registerHandler(ToggleSetupAssistantPacket.class, (packet, proxy, connection, direction) -> {
            String name = packet.getName();
            if (name == null) editing = null;
            else editing = WarpSystem.proxy().getPlayer(name).orElse(null);
        });
    }

    @Subscribe (order = PostOrder.FIRST)
    public void onChatRemove(PlayerChatEvent e) {
        if (editing == null) return;

        Player c = e.getPlayer();
        if (c.equals(editing)) {
            //sender is editing
            message = e.getMessage();
            e.setResult(PlayerChatEvent.ChatResult.message(""));
        }
    }

    @Subscribe (order = PostOrder.LAST)
    public void onChatAdd(PlayerChatEvent e) {
        if (editing == null) return;

        Player c = e.getPlayer();
        if (c.equals(editing)) {
            //sender is editing
            e.setResult(PlayerChatEvent.ChatResult.message(message));
            message = null;
        }
    }

    @Subscribe
    public void onQuit(DisconnectEvent e) {
        if (e.getPlayer().equals(editing)) this.editing = null;
    }
}
