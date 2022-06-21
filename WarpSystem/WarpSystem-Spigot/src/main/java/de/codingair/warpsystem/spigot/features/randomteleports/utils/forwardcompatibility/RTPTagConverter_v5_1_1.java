package de.codingair.warpsystem.spigot.features.randomteleports.utils.forwardcompatibility;

import de.codingair.warpsystem.spigot.api.files.TagConverter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;

public class RTPTagConverter_v5_1_1 extends TagConverter {
    public RTPTagConverter_v5_1_1() {
        super(WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/", false), WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/", false));

        boolean enabled = from.getConfig().getBoolean("RandomTeleport.Support.Biome.Enabled");
        List<String> biomes = from.getConfig().getStringList("RandomTeleport.Support.Biome.BiomeList");

        if (!biomes.isEmpty()) {
            List<String> converted = new ArrayList<>();

            if (enabled) {
                for (Biome value : Biome.values()) {
                    if (!biomes.contains(value.name())) converted.add(value.name());
                }

                WarpSystem.getInstance().getLogger().warning("The biome list of random teleports has been converted to a blacklist! Please double check your RandomTP.yml to avoid issues.");
            }

            to.getConfig().set("RandomTeleport.Support.Biome.Blacklist", converted);
            to.saveConfig();
        }

        WarpSystem.getInstance().getFileManager().unloadFile(super.from);
        WarpSystem.getInstance().getFileManager().unloadFile(super.to);
    }
}
