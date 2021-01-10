package de.codingair.warpsystem.bungee.base;

import de.codingair.codingapi.bungeecord.BungeeAPI;
import de.codingair.codingapi.bungeecord.files.FileManager;
import de.codingair.codingapi.tools.time.TimeFetcher;
import de.codingair.codingapi.tools.time.Timer;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.bungee.api.chatinput.ChatInputManager;
import de.codingair.warpsystem.bungee.base.commands.CWarpSystem;
import de.codingair.warpsystem.bungee.base.listeners.MainListener;
import de.codingair.warpsystem.bungee.base.listeners.PlayerDataListener;
import de.codingair.warpsystem.bungee.base.listeners.SetupAssistantListener;
import de.codingair.warpsystem.bungee.base.managers.CooldownManager;
import de.codingair.warpsystem.bungee.base.managers.DataManager;
import de.codingair.warpsystem.bungee.base.managers.JarManager;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import de.codingair.warpsystem.bungee.base.utils.Lang;
import de.codingair.warpsystem.bungee.transfer.BungeeHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

public class WarpSystem extends Plugin implements Proxy {
    public static final String PERMISSION_MODIFY_SYSTEM = "warpsystem.modify.system";

    private static WarpSystem instance;
    private final BungeeHandler dataHandler = new BungeeHandler(this);
    private final FileManager fileManager = new FileManager(this);
    private final DataManager dataManager = new DataManager();
    private final JarManager jarManager = new JarManager();
    private final Timer timer = new Timer();
    private ServerManager serverManager;
    private CooldownManager cooldownManager;
    private PlayerDataListener playerDataListener;

    public static void log(String message) {
        System.out.println(message);
    }

    @Override
    public void onEnable() {
        instance = this;
        timer.start();

        BungeeAPI.getInstance().onEnable(this);

        log(" ");
        log("________________________________________________________");
        log(" ");
        log("                   WarpSystem [" + getDescription().getVersion() + "]");
        log(" ");
        log("Status:");
        log(" ");
        dataManager.preLoad();
        log("Initialize SpigotConnector");
        this.dataHandler.onEnable();

        this.fileManager.loadFile("Config", "/", "de/codingair/warpsystem/bungee/");
        try {
            Lang.initPreDefinedLanguages(this);
        } catch(IOException e) {
            e.printStackTrace();
        }

        //listener
        getProxy().getPluginManager().registerListener(this, new MainListener());
        getProxy().getPluginManager().registerListener(this, cooldownManager = new CooldownManager());

        cooldownManager.load();

        getProxy().getPluginManager().registerListener(this, new SetupAssistantListener());
        getProxy().getPluginManager().registerListener(this, (playerDataListener = new PlayerDataListener()));

        this.serverManager = new ServerManager();
        this.serverManager.run();

        new ChatInputManager();

        getProxy().getPluginManager().registerCommand(this, new CWarpSystem());

        log("Loading features");
        boolean createBackup = false;
        if(!this.dataManager.load(false)) createBackup = true;

        if(createBackup) {
            log("Loading with errors > Create backup...");
            createBackup();
            log("Backup successfully created");
        }

        this.startAutoSaver();

        log(" ");
        log("Done (" + timer.result() + ")");
        log(" ");
        log("________________________________________________________");
        log(" ");
    }

    @Override
    public void onDisable() {
        this.dataHandler.flush();
        this.dataHandler.onDisable();
        save(false);
        destroy();
        BungeeAPI.getInstance().onDisable(this);
    }

    private void startAutoSaver() {
        WarpSystem.log("Starting AutoSaver");
        getProxy().getScheduler().schedule(this, () -> save(true), 10, 10, TimeUnit.MINUTES);
    }

    private void destroy() {
        this.dataManager.getManagers().forEach(Manager::destroy);
    }

    private void save(boolean saver) {
        try {
            if(!saver) {
                timer.start();

                log(" ");
                log("________________________________________________________");
                log(" ");
                log("                   WarpSystem [" + getDescription().getVersion() + "]");
                log(" ");
                log("Status:");
                log(" ");
            }

            if(!saver) log("Saving features");
            this.cooldownManager.save();

            this.dataManager.save(saver);

            if(!saver) {
                log(" ");
                log("Done (" + timer.result() + ")");
                log(" ");
                log("________________________________________________________");
                log(" ");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createBackup() {
        getDataFolder().mkdir();

        File backupFolder = new File(getDataFolder().getPath() + "/Backups/", TimeFetcher.getYear() + "_" + (TimeFetcher.getMonthNum() + 1) + "_" + TimeFetcher.getDay() + " " + TimeFetcher.getHour() + "_" + TimeFetcher.getMinute() + "_" + TimeFetcher.getSecond());
        backupFolder.mkdirs();

        for(File file : getDataFolder().listFiles()) {
            if(file.getName().equals("Backups") || file.getName().equals("ErrorReport.txt")) continue;
            File dest = new File(backupFolder, file.getName());

            try {
                if(file.isDirectory()) {
                    copyFolder(file, dest);
                    continue;
                }

                copyFileUsingFileChannels(file, dest);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFolder(File source, File dest) throws IOException {
        dest.mkdirs();
        for(File file : source.listFiles()) {
            File copy = new File(dest, file.getName());

            if(file.isDirectory()) {
                copyFolder(file, copy);
                continue;
            }

            copyFileUsingFileChannels(file, copy);
        }
    }

    private void copyFileUsingFileChannels(File source, File dest) throws IOException {
        try(FileChannel inputChannel = new FileInputStream(source).getChannel(); FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    public static BungeeHandler getDataHandler() {
        return getInstance().dataHandler;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public JarManager getJarManager() {
        return jarManager;
    }

    public static ProxyServer proxy() {
        return instance.getProxy();
    }

    public static WarpSystem getInstance() {
        return instance;
    }

    public PlayerDataListener getPlayerListener() {
        return playerDataListener;
    }
}
