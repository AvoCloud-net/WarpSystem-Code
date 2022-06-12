package de.codingair.warpsystem.spigot.base.setupassistant.utils;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.versions.Messager_v1_17;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.versions.Messager_v1_19;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.versions.Messager_v1_8;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Messager {

    void startBlocking();

    void stopBlocking();

    void flushCache();

    void sendMessage(@NotNull TextComponent tc);

    void sendMessage(@NotNull SimpleMessage message);

    void queue(@NotNull Object packet);

    @NotNull
    static Messager get(@NotNull Player player, @NotNull SetupAssistant assistant) {
        if (Version.atLeast(19)) return new Messager_v1_19(player, assistant);
        else if (Version.atLeast(17)) return new Messager_v1_17(player, assistant);
        else return new Messager_v1_8(player, assistant);
    }
}
