package de.codingair.warpsystem.spigot.features.randomteleports.managers;

import com.google.common.base.Preconditions;
import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.AvailableForSetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.Function;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.ProxyFeature;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.randomteleports.listeners.InteractListener;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.WorldOption;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility.RTPTagConverter_v4_2_2;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility.RTPTagConverter_v4_2_6;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@AvailableForSetupAssistant (type = "Random teleports", config = "RTPConfig")
@Function (name = "Enabled", defaultValue = "true", config = "Config", configPath = "WarpSystem.Functions.RandomTeleports", clazz = Boolean.class)
@Function (name = "Protected regions", defaultValue = "true", configPath = "RandomTeleport.Support.ProtectedRegions", clazz = Boolean.class)
@Function (name = "World border", defaultValue = "true", configPath = "RandomTeleport.Support.WorldBorder", clazz = Boolean.class)
@Function (name = "Block blacklist", defaultValue = "true", configPath = "RandomTeleport.Block_Blacklist.Enabled", description = "§eBlocks §7» §6RTPConfig.yml", clazz = Boolean.class)
@Function (name = "Biome filter", defaultValue = "false", configPath = "RandomTeleport.Support.Biome.Enabled", description = "§eBiomes §7» §6RTPConfig.yml", clazz = Boolean.class)
@Function (name = "Max uses", defaultValue = "4", configPath = "RandomTeleport.Max", description = "§cONLY §rif permissions in the main §eConfig.yml §rare §cdisabled", clazz = Integer.class)
@Function (name = "Free uses", defaultValue = "1", configPath = "RandomTeleport.Free", description = "§cONLY §rif permissions in the main §eConfig.yml §rare §cdisabled", clazz = Integer.class)
public abstract class RandomTeleportManager implements Manager, ProxyFeature {
    protected final List<Material> materialBlackList = new ArrayList<>();
    protected final List<WorldOption> worldOptions = new ArrayList<>();
    protected final HashMap<Player, RandomLocationCalculator> searching = new HashMap<>();
    protected final HashMap<String, List<String>> worlds = new HashMap<>();
    protected final List<Location> interactBlocks = new ArrayList<>();
    protected final InteractListener listener = new InteractListener();
    protected boolean buyable;
    protected double costs;
    protected boolean protectedRegions;
    protected List<Biome> biomeList;
    protected WorldOption defValues;
    protected int netherHeight;
    protected int endHeight;
    protected int max;
    protected int free;

    public static RandomTeleportManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.RANDOM_TELEPORTS);
    }

    @Override
    public void preLoad() {
        new RTPTagConverter_v4_2_2();
        new RTPTagConverter_v4_2_6();
    }

    public abstract RandomLocationCalculator newCalculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<Location> callback);

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Teleporters");
        UTFConfig config = file.getConfig();

        if (!saver) WarpSystem.log("  > Saving RandomTeleporters");

        List<JSON> interactBlocks = new ArrayList<>();
        for (Location l : this.interactBlocks) {
            JSON json = new JSON();
            l.trim(0);
            l.write(json);
            interactBlocks.add(json);
        }

        config.set("RandomTeleporter.InteractBlocks", interactBlocks);
        file.saveConfig();
        if (!saver) WarpSystem.log("    ...saved " + interactBlocks.size() + " InteractBlock(s)");
    }

    @Override
    public void onConnect() {
        List<String> worlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (world == null) continue;

            WorldOption option = getOption(world, defValues);
            if (option.isDisabled()) continue;

            worlds.add(world.getName());
        }

        WarpSystem.getDataHandler().send(new RandomTPWorldsPacket(worlds));
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void destroy() {
        this.interactBlocks.clear();
        if (this.biomeList != null) this.biomeList.clear();
        this.materialBlackList.clear();
        HandlerList.unregisterAll(this.listener);
    }

    public boolean canBuy(Player player) {
        if (player.isOp()) return true;

        int bought = getInstance().getBoughtTeleports(player);
        int free = getInstance().getFreeTeleportAmount(player);
        int max = getInstance().getMaxTeleportAmount(player);

        return max == -1 || max - free - bought > 0;
    }

    public boolean canTeleport(Player player) {
        if (player.isOp()) return true;

        UUID u = WarpSystem.getInstance().getPlayerDataManager().get(player);
        int bought = getInstance().getBoughtTeleports(u);
        int teleports = getInstance().getTeleports(u);
        int free = getInstance().getFreeTeleportAmount(player);

        return free == -1 || free + bought - teleports > 0;
    }

    public int getMaxTeleportAmount(Player player) {
        if (player.isOp()) return -1;

        if (WarpSystem.PERMISSION_USE_RANDOM_TELEPORTER != null) {
            int amount = 0;
            for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
                if (!effectivePermission.getValue()) continue;
                String perm = effectivePermission.getPermission();

                if (perm.equals("*") || perm.toLowerCase().startsWith("warpsystem.*")
                        || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

                if (perm.toLowerCase().startsWith("warpsystem.randomteleporters.max.")) {
                    String s = perm.substring(33);
                    if (s.equals("*") || s.equalsIgnoreCase("n")) return -1;

                    try {
                        int i = Integer.parseInt(s);
                        if (i > amount) amount = i;
                    } catch (Throwable ignored) {
                    }
                }

            }

            return amount;
        } else return max;
    }

    public int getFreeTeleportAmount(Player player) {
        if (player.isOp()) return -1;

        if (WarpSystem.PERMISSION_USE_RANDOM_TELEPORTER != null) {
            int amount = 0;
            for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
                if (!effectivePermission.getValue()) continue;
                String perm = effectivePermission.getPermission();

                if (perm.equals("*") || perm.toLowerCase().startsWith("warpsystem.*")
                        || perm.toLowerCase().startsWith("warpsystem.randomteleporters.*")) return -1;

                if (perm.toLowerCase().startsWith("warpsystem.randomteleporters.free.")) {
                    String s = perm.substring(34);
                    if (s.equals("*") || s.equalsIgnoreCase("n")) return -1;

                    try {
                        int i = Integer.parseInt(s);
                        if (i > amount) amount = i;
                    } catch (Throwable ignored) {
                    }
                }
            }
            return amount;
        } else return free;
    }

    public WorldOption getOption(World world, WorldOption def) {
        for (WorldOption worldOption : this.worldOptions) {
            if (worldOption.getWorldName().equalsIgnoreCase(world.getName())) return worldOption;
        }

        return def;
    }

    public void tryToTeleport(Player player) {
        tryToTeleport(player.getName(), player.getWorld(), false, new Callback<Integer>() {
            @Override
            public void accept(Integer object) {
            }
        });
    }

    public void search(Player player, World target, WorldOption option, Callback<Location> callback) {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(option);

        if (option.isDisabled()) {
            callback.accept(null);
            return;
        }

        org.bukkit.Location start = new Location();
        option.prepareStart(start, target);

        RandomLocationCalculator t = newCalculator(player, start, option.getMin(), option.getMax(), new Callback<Location>() {
            @Override
            public void accept(Location loc) {
                searching.remove(player);

                if (loc != null) {
                    loc.setYaw(player.getLocation().getYaw());
                    loc.setPitch(player.getLocation().getPitch());
                }

                callback.accept(loc);
            }
        });
        searching.put(player, t);
        Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), t);
    }

    public void tryToTeleport(String targetPlayer, World target, boolean force, Callback<Integer> callback) {
        Player player = Bukkit.getPlayerExact(targetPlayer);

        if (player == null) {
            callback.accept(1);
            return;
        }

        RandomLocationCalculator c;
        if ((c = searching.get(player)) != null) {
            if (System.currentTimeMillis() - c.getLastReaction() > 5000) {
                searching.remove(player);
                player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
            } else player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Already_Searching"));

            callback.accept(-1);
            return;
        }

        if (!canTeleport(player) && !force) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
            callback.accept(4);
            return;
        }

        WorldOption option = getOption(target, defValues);

        if (option.isDisabled()) {
            player.sendMessage(Lang.getPrefix() + Lang.get("RTP_Not_available_in_this_world"));
            return;
        }

        search(player, target, option, new Callback<Location>() {
            @Override
            public void accept(Location loc) {
                if (loc == null) {
                    //no location found, try again
                    player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                    callback.accept(2);
                } else {
                    //teleported
                    UUID uuid = WarpSystem.getInstance().getPlayerDataManager().get(player);
                    if (!player.isOp()) increaseTeleports(uuid);

                    Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(loc)), "");
                        options.setOrigin(Origin.RandomTP);
                        options.setMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported"));
                        options.setSkip(true);
                        options.addCallback(new Callback<Result>() {
                            @Override
                            public void accept(Result object) {
                                callback.accept(0);
                            }
                        });

                        WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                    });
                }
            }
        });

        player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Searching"));
    }

    public void increaseTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();
        int i = config.getInt("RandomTeleporter." + uuid.toString() + ".Teleports", 0) + 1;
        config.set("RandomTeleporter." + uuid.toString() + ".Teleports", i);
        file.saveConfig();
    }

    public int getTeleports(Player player) {
        return getTeleports(WarpSystem.getInstance().getPlayerDataManager().get(player));
    }

    public int getTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();

        return config.getInt("RandomTeleporter." + uuid.toString() + ".Teleports", 0);
    }

    public void setBoughtTeleports(UUID uuid, int teleports) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();
        config.set("RandomTeleporter." + uuid.toString() + ".Bought", teleports);
        file.saveConfig();
    }

    public int getBoughtTeleports(Player player) {
        return getBoughtTeleports(WarpSystem.getInstance().getPlayerDataManager().get(player));
    }

    public int getBoughtTeleports(UUID uuid) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("PlayData");
        UTFConfig config = file.getConfig();

        return config.getInt("RandomTeleporter." + uuid.toString() + ".Bought", 0);
    }

    public void updateWorlds(HashMap<String, List<String>> data) {
        this.worlds.putAll(data);
    }

    public List<String> getWorlds(@NotNull String server) {
        return this.worlds.get(server.toLowerCase());
    }

    public double getCosts() {
        return costs;
    }

    public WorldOption getDefValues() {
        return defValues;
    }

    public boolean isProtectedRegions() {
        return protectedRegions;
    }

    public List<Biome> getBiomeList() {
        return biomeList;
    }

    public List<Location> getInteractBlocks() {
        return interactBlocks;
    }

    public InteractListener getListener() {
        return listener;
    }

    public boolean isBuyable() {
        return buyable && Bank.isReady();
    }

    public int getNetherHeight() {
        return netherHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public List<Material> getMaterialBlackList() {
        return materialBlackList;
    }

    public boolean hasRegisteredServers() {
        return !this.worlds.isEmpty();
    }

    public Set<String> getServer() {
        return this.worlds.keySet();
    }
}
