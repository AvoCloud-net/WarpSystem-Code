package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;

public enum Origin {
    WarpIcon(Icon.class, "WarpGUI"),
    GlobalWarp(null, "GlobalWarps"),
    SimpleWarp(null, "SimpleWarps"),
    WarpSign(de.codingair.warpsystem.spigot.features.signs.utils.WarpSign.class, "WarpSigns"),
    ShortCut(Shortcut.class, "Shortcuts"),
    CommandBlock(null, "CommandBlocks"),
    TeleportCommand,
    Custom,
    TeleportRequest,
    PlayerWarp(de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp.class, "PlayerWarps"),
    Portal(de.codingair.warpsystem.spigot.features.portals.utils.Portal.class, "Portals"),
    Spawn(de.codingair.warpsystem.spigot.features.spawn.utils.Spawn.class, "Spawn"),
    RandomTP(null, "RandomTp"),
    TeleportInterception,
    UNKNOWN;

    private final Class<? extends FeatureObject> featureClass;
    private final String configName;

    Origin() {
        this(null, null);
    }

    Origin(Class<? extends FeatureObject> featureClass, String configName) {
        this.featureClass = featureClass;
        this.configName = configName;
    }

    public static Origin getByClass(FeatureObject object) {
        for (Origin value : values()) {
            if(value.featureClass == null) continue;
            if (value.featureClass.isInstance(object)) return value;
        }

        return UNKNOWN;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean sendTeleportMessage() {
        if (configName == null) return true;
        return WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Send.Teleport_Message." + getConfigName(), true);
    }

    public long getCooldown() {
        return WarpSystem.opt().getCooldown(this);
    }
}
