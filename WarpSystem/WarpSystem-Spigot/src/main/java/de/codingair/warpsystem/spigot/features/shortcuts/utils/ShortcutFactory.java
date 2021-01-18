package de.codingair.warpsystem.spigot.features.shortcuts.utils;

import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.versionfactory.VKey;
import de.codingair.warpsystem.spigot.versionfactory.specified.FactoryBuilder;
import org.jetbrains.annotations.NotNull;

public class ShortcutFactory extends FactoryBuilder<Shortcut> {
    private static final ShortcutFactory F = new ShortcutFactory();

    private ShortcutFactory() {
        super(VKey.Shortcut);
    }

    public static Shortcut build() {
        return F.newInstance();
    }

    public static Shortcut build(@NotNull Destination destination, @NotNull String displayName) {
        return F.newInstance(destination, displayName);
    }
}
