package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.hotbar.components.SyncItemComponent;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.hotbar.BackItem;
import de.codingair.warpsystem.spigot.base.guis.editor.hotbar.PlaceholderItem;
import de.codingair.warpsystem.spigot.base.guis.editor.pages.DestinationPage;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.VelocityAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class VelocityHotbarEditor extends HotbarGUI {
    private final DestinationPage fallback;
    private Location from = null;

    public VelocityHotbarEditor(DestinationPage fallback, Player player) {
        super(player, WarpSystem.getInstance(), 2);
        this.fallback = fallback;
        initialize();
    }

    @Override
    public void initialize() {

        addItem(new BackItem(fallback));
        addItem(new PlaceholderItem());

        addItem(new SyncItemComponent(new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                VelocityAdapter adapter = checkDestinationType();

                Vector vector = player.getLocation().getDirection().normalize();
                adapter.setVector(vector);
                updateSingle(2);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                Vector vector = getVector();

                if (vector != null) {
                    Location location = new Location(null, 0, 0, 0);
                    location.setDirection(vector);
                    String direction = "§7→ §e" + Math.round(location.getYaw() * 100) / 100 + "° §7| ↑ §e" + Math.round(location.getPitch() * 100) / 100 + "°";

                    MessageAPI.sendActionBar(player, direction, WarpSystem.getInstance(), Integer.MAX_VALUE);
                }
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ARROW);
                builder.setName("§7" + Lang.get("Leftclick") + ": §e" + Lang.get("Set"));
                return builder.getItem();
            }
        });

        addItem(new SyncItemComponent(new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                VelocityAdapter adapter = checkDestinationType();

                if (clickType == ClickType.LEFT_CLICK) {
                    adapter.setMultiplier(adapter.getMultiplier() - 0.1);
                    updateSingle(3);
                } else if (clickType == ClickType.RIGHT_CLICK) {
                    adapter.setMultiplier(adapter.getMultiplier() + 0.1);
                    updateSingle(3);
                } else if (clickType == ClickType.SHIFT_LEFT_CLICK) {
                    adapter.setMultiplier(adapter.getMultiplier() - 0.01);
                    updateSingle(3);
                } else if (clickType == ClickType.SHIFT_RIGHT_CLICK) {
                    adapter.setMultiplier(adapter.getMultiplier() + 0.01);
                    updateSingle(3);
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                MessageAPI.sendActionBar(player, StringFormatter.MINUS_PLUS_SHIFT(Lang.get("Power")), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(player);
            }
        }) {
            @Override
            public ItemStack craftItem() {
                Double multiplier = getMultiplier();
                String description;

                if (multiplier != null) description = multiplier + "";
                else description = "§c-";

                ItemBuilder builder = new ItemBuilder(XMaterial.GUNPOWDER);
                builder.setName("§7" + Lang.get("Power") + ": §e" + description);
                return builder.getItem();
            }
        });

        addItem(new SyncItemComponent(new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if (!canTry()) return;

                VelocityAdapter adapter = checkDestinationType();

                if (from == null) {
                    Vector velocity = adapter.getVector();
                    from = player.getLocation();
                    from.setDirection(velocity);

                    velocity.multiply(adapter.getMultiplier());
                    player.setVelocity(velocity);
                } else {
                    player.teleport(from);
                    from = null;
                }

                updateSingle(4);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
            }
        }) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.BLAZE_POWDER);
                builder.setName("§7" + Lang.get("Leftclick") + ": " + (canTry() ? "§e" : "§7") + Lang.get(from == null ? "Try" : "Reset"));
                return builder.getItem();
            }
        });
    }

    private boolean canTry() {
        return getVector() != null;
    }

    private VelocityAdapter checkDestinationType() {
        Destination destination = fallback.getDestination();

        if (destination.getType() != DestinationType.Velocity) {
            destination.setAdapter(new VelocityAdapter(new Vector(), 1D));
            destination.setType(DestinationType.Velocity);
            destination.setId(null);
            updateSingle(2);
            updateSingle(3);
            updateSingle(4);
        }

        return (VelocityAdapter) destination.getAdapter();
    }

    private @Nullable Vector getVector() {
        Destination destination = fallback.getDestination();

        if (destination.getType() != DestinationType.Velocity) return null;

        return ((VelocityAdapter) destination.getAdapter()).getVector();
    }

    private @Nullable Double getMultiplier() {
        Destination destination = fallback.getDestination();

        if (destination.getType() != DestinationType.Velocity) return null;

        return ((VelocityAdapter) destination.getAdapter()).getMultiplier();
    }
}
