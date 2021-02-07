package de.codingair.warpsystem.spigot.api;

import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.warpsystem.spigot.api.blocks.listeners.RuleListener;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReaderListener;
import de.codingair.warpsystem.spigot.api.packetreader.GlobalPacketReaderManager;
import de.codingair.warpsystem.spigot.api.packetreader.readers.TeleportReader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotAPI {
    private static SpigotAPI instance;
    private Boolean bungeeCord = null;

    private final GlobalPacketReaderManager globalPacketReaderManager = new GlobalPacketReaderManager();

    public static SpigotAPI getInstance() {
        if (instance == null) instance = new SpigotAPI();
        return instance;
    }

    public static boolean bungeeCord() {
        if (instance.bungeeCord != null) return instance.bungeeCord;

        try {
            IReflection.FieldAccessor<Boolean> bungee = IReflection.getField(Class.forName("org.spigotmc.SpigotConfig"), "bungee");
            instance.bungeeCord = bungee.get(null);
        } catch (ClassNotFoundException ex) {
            instance.bungeeCord = Bukkit.spigot().getConfig().getBoolean("settings.bungeecord");
        }

        return instance.bungeeCord;
    }

    public void onEnable(JavaPlugin plugin) {
        try {
            this.globalPacketReaderManager.register(new TeleportReader(), false);
        } catch (ClassNotFoundException ignored) {
        }

        Bukkit.getPluginManager().registerEvents(new RuleListener(), plugin);
        this.globalPacketReaderManager.onEnable();
        Bukkit.getPluginManager().registerEvents(new GlobalPacketReaderListener(), plugin);
    }

    public void onDisable() {
        this.globalPacketReaderManager.onDisable();
    }

    public GlobalPacketReaderManager getGlobalPacketReaderManager() {
        return globalPacketReaderManager;
    }
}
