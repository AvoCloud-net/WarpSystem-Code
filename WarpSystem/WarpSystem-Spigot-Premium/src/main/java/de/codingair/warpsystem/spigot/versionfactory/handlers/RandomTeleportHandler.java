package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.tools.Area;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.ConfigMask;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.core.transfer.packets.spigot.QueueRTPUsagePacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportUtils;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.CRandomTp;
import de.codingair.warpsystem.spigot.features.randomteleports.listeners.SpawnListener;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.WorldOption;
import de.codingair.warpsystem.spigot.transfer.handlers.QueueRTPUsagePacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RandomTeleportHandler extends RandomTeleportManager {
    protected boolean worldBorder;

    @Override
    public boolean load(boolean loader) {
        ConfigFile rtpFile = WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/");
        UTFConfig config = rtpFile.getConfig();

        WarpSystem.log("  > Loading RandomTeleporters");

        this.buyable = config.getBoolean("RandomTeleport.Buyable.Enabled", true);
        this.costs = config.getDouble("RandomTeleport.Buyable.Costs", 500.0);

        this.concurrent = config.getInt("RandomTeleport.Concurrent_Teleports", 5);

        this.max = config.getInt("RandomTeleport.Max", 4);
        this.free = config.getInt("RandomTeleport.Free", 1);

        if (this.defValues != null) this.defValues.destroy();
        this.defValues = new WorldOption("§DEF§");
        ConfigMask w = new ConfigMask(rtpFile, "RandomTeleport.Worlds.Default");
        this.defValues.read(w);

        this.netherHeight = config.getInt("RandomTeleport.Range.Highest_Y.Nether", 126);
        this.endHeight = config.getInt("RandomTeleport.Range.Highest_Y.End", 72);

        this.materialBlackList.clear();
        if (config.getBoolean("RandomTeleport.Block_Blacklist.Enabled", false)) {
            for (String material : config.getStringList("RandomTeleport.Block_Blacklist.List")) {
                Optional<XMaterial> parsed = XMaterial.matchXMaterial(material.toUpperCase().replace(" ", "_"));
                parsed.ifPresent(xMaterial -> {
                    Material m = xMaterial.parseMaterial();

                    if (!materialBlackList.contains(m)) materialBlackList.add(m);
                });
            }
        }

        this.protectedRegions = config.getBoolean("RandomTeleport.Support.ProtectedRegions", true);
        this.worldBorder = config.getBoolean("RandomTeleport.Support.WorldBorder", true);
        if (config.getBoolean("RandomTeleport.Support.Biome.Enabled", true)) {
            List<String> configBiomes = config.getStringList("RandomTeleport.Support.Biome.BiomeList");
            biomeList = new ArrayList<>();

            if (configBiomes.isEmpty()) {
                for (Biome value : Biome.values()) {
                    if (value.name().equalsIgnoreCase("VOID")) continue;
                    this.biomeList.add(value);
                }
            } else {
                for (String biome : configBiomes) {
                    for (Biome value : Biome.values()) {
                        if (value.name().equalsIgnoreCase(biome) && !biomeList.contains(value)) {
                            biomeList.add(value);
                            break;
                        }
                    }
                }
            }
        }

        SpawnListener listener = new SpawnListener();
        Bukkit.getPluginManager().registerEvents(listener, WarpSystem.getInstance());
        WarpSystem.getDataHandler().registerHandler(QueueRTPUsagePacket.class, new QueueRTPUsagePacketHandler());

        boolean success = true;
        worldOptions.clear();
        List<?> l = config.getList("RandomTeleport.Worlds.Options");
        if (l != null)
            for (Object data : l) {
                try {
                    JSON json = new JSON((Map<?, ?>) data);
                    for (Object o : json.keySet(false)) {
                        String key = o + "";
                        WorldOption option = new WorldOption(key);
                        json.getSerializable(key, option);
                        worldOptions.add(option);
                    }
                } catch (Exception e) {
                    success = false;
                    e.printStackTrace();
                }
            }

        WarpSystem.log("    ...got " + this.worldOptions.size() + " WorldOption(s)");
        ConfigFile file = WarpSystem.getInstance().getFileManager().loadFile("Teleporters", "/Memory/");
        config = file.getConfig();

        l = config.getList("RandomTeleporter.InteractBlocks");
        if (l != null)
            for (Object s : l) {
                if (s instanceof Map) {
                    JSON json = new JSON((Map<?, ?>) s);
                    Location loc = new Location();
                    try {
                        loc.read(json);
                    } catch (Exception e) {
                        success = false;
                        e.printStackTrace();
                        continue;
                    }

                    this.interactBlocks.add(loc);
                } else if (s instanceof String) {
                    this.interactBlocks.add(Location.getByJSONString((String) s));
                }
            }

        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());
        new CRandomTp().register();

        WarpSystem.log("    ...got " + this.interactBlocks.size() + " InteractBlock(s)");
        WarpSystem.getInstance().getProxyFeatureList().add(this);

        return success;
    }

    @Override
    public RandomLocationCalculator newCalculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<RandomLocationCalculator> callback) {
        return new Calculator(player, location, minRange, maxRange, callback);
    }

    public boolean isWorldBorder() {
        return worldBorder;
    }

    public static class Calculator extends RandomLocationCalculator {
        public Calculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<RandomLocationCalculator> callback) {
            super(player, location, minRange, maxRange, callback);
        }

        public boolean correct(Location location, boolean safety) {
            if (RandomTeleportManager.getInstance().getBiomeList() != null && !RandomTeleportManager.getInstance().getBiomeList().contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockZ())))
                return false;
            if (RandomTeleportManager.getInstance().isProtectedRegions() && isProtected(location).join()) return false;
            if (((RandomTeleportHandler) RandomTeleportManager.getInstance()).isWorldBorder() && !isInsideOfWorldBorder(location)) return false;
            if (safety) {
                Location above = location.clone();
                above.setY(above.getY() + 1);
                Location below = location.clone();
                below.setY(below.getY() - 1);

                return isEnoughSpace(location) && TeleportUtils.isSafe(above.getBlock()) && TeleportUtils.isSafe(location.getBlock()) && TeleportUtils.isSafe(below.getBlock());
            } else return true;
        }

        private boolean isInsideOfWorldBorder(Location location) {
            WorldBorder border = location.getWorld().getWorldBorder();
            return Area.isInArea(location, border.getCenter(), border.getSize() / 2, false, 0);
        }
    }
}
