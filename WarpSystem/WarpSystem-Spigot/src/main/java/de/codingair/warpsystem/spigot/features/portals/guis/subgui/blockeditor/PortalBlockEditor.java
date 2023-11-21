package de.codingair.warpsystem.spigot.features.portals.guis.subgui.blockeditor;

import de.codingair.codingapi.API;
import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.Removable;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import de.codingair.warpsystem.spigot.features.portals.utils.Portal;
import de.codingair.warpsystem.spigot.features.portals.utils.PortalBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PortalBlockEditor implements Removable {
    private final UUID uniqueId = UUID.randomUUID();
    private final Player player;
    private final Portal portal;
    private final List<Block> alignTo = new ArrayList<>();
    private final FastEditingTool fastEditingTool;
    private final CustomPortalBlockTool customPortalBlockTool;
    private boolean ended = false;
    private ItemStack[] old;
    private BukkitRunnable alignRunnable;
    private boolean show = true;

    public PortalBlockEditor(Player player, Portal portal) {
        this.player = player;
        API.addRemovable(this);
        this.portal = portal;
        this.fastEditingTool = new FastEditingTool(this, player);
        this.customPortalBlockTool = new CustomPortalBlockTool(this, player);

        this.alignRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!show) return;

                for (Block b : alignTo) {
                    Particle.VILLAGER_HAPPY.send(b.getLocation().add(0.5, 0.5, 0.5), player);
                }
            }
        };

        this.alignRunnable.runTaskTimer(WarpSystem.getInstance(), 5, 5);
    }

    @Override
    public void destroy() {
        this.end();
    }

    public FastEditingTool getFastEditingTool() {
        return fastEditingTool;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public JavaPlugin getPlugin() {
        return WarpSystem.getInstance();
    }

    public void init() {
        this.old = new ItemStack[9];

        for (int i = 0; i < 9; i++) {
            old[i] = this.player.getInventory().getItem(i);
            this.player.getInventory().setItem(i, new ItemStack(Material.AIR));
        }

        this.player.getInventory().setItem(0, fastEditingTool);

        int slot = 2;
        for (BlockType value : BlockType.values()) {
            if (value == BlockType.CUSTOM) continue;
            this.player.getInventory().setItem(slot++, value.getEditMaterial().setName(value.getName()).getItem());
        }

        for (PortalBlock block : portal.getBlocks()) {
            if (block.getType() == BlockType.CUSTOM) {
                this.alignTo.add(block.getLocation().getBlock());
            }
        }

        this.player.getInventory().setItem(8, customPortalBlockTool);

        if (this.player.getInventory().getHeldItemSlot() == 0) {
            fastEditingTool.onHover(null);
        } else {
            MessageAPI.sendActionBar(player, Lang.get("Drop_To_Leave"), WarpSystem.getInstance(), Integer.MAX_VALUE);
        }

        if (this.player.getInventory().getHeldItemSlot() == 8) {
            customPortalBlockTool.onHover(null);
        }
    }

    @Nullable
    public static Block getSupportTargetMaterial(@NotNull Player player) {
        Block b = player.getTargetBlock((Set<Material>) null, 10);

        if (b != null && b.getType() != XMaterial.AIR.parseMaterial() && b.getType() != XMaterial.VOID_AIR.parseMaterial() && b.getType() != XMaterial.CAVE_AIR.parseMaterial() && b.getType() != XMaterial.CHEST.parseMaterial() && b.getType() != XMaterial.TRAPPED_CHEST.parseMaterial()) {
            if (Version.atLeast(14)) {
                if (b.isPassable()) return b;
                else {
                    BoundingBox bb = b.getBoundingBox();
                    if (bb.getWidthX() < 0.9 || bb.getWidthZ() < 0.9 || bb.getHeight() < 0.9) return b;

                    if (Version.atLeast(17)) {
                        // assume that we only use multiple bounding boxes to allow some clearance
                        if (b.getCollisionShape().getBoundingBoxes().size() > 1) return b;
                    }
                }
            } else {
                // 1.13 and below
                Material m = b.getType();

                IReflection.MethodAccessor isFuel = IReflection.getSaveMethod(Material.class, "isFuel", boolean.class);
                boolean fuel = isFuel != null && (boolean) isFuel.invoke(m);

                if (!m.isOccluding() && (fuel || !m.isSolid())) return b;
            }
        }

        return null;
    }

    public void update() {
        int slot = 2;
        boolean fastEditing = fastEditingTool.locationsSet();
        for (BlockType value : BlockType.values()) {
            if (value == BlockType.CUSTOM) continue;
            this.player.getInventory().setItem(slot++, value.getEditMaterial().setName(value.getName() + (fastEditing ? "§8 (§e" + Lang.get("Fast_Editing") + "§8)" : "")).getItem());
        }
    }

    public Portal end() {
        if (ended) return null;
        ended = true;

        if (alignRunnable != null) {
            alignRunnable.cancel();
            alignRunnable = null;

            setAlignBlocks(false);
            alignTo.clear();
        }

        List<PlayerItem> items = API.getRemovables(getPlayer(), PlayerItem.class);
        for (PlayerItem item : items) {
            item.destroy();
        }
        items.clear();

        int slot = 0;
        for (ItemStack itemStack : old) {
            this.player.getInventory().setItem(slot++, itemStack == null ? new ItemStack(Material.AIR) : itemStack);
        }
        this.player.updateInventory();

        API.removeRemovable(this);
        return portal;
    }

    public PortalBlock addPosition(Location location, BlockType type) {
        PortalBlock block = new PortalBlock(new de.codingair.codingapi.tools.Location(location), type);
        this.portal.addPortalBlock(block);
        update();
        return block;
    }

    public boolean removePosition(Location location) {
        PortalBlock block = null;

        for (PortalBlock b : this.portal.getBlocks()) {
            if (b.getLocation().equals(location)) {
                block = b;
                break;
            }
        }

        if (block != null) {
            portal.removePortalBlock(block);
            update();
            return true;
        }

        return false;
    }

    public void setAlignBlocks(boolean show) {
        this.show = show;

        List<Block> alignTo = new ArrayList<>(this.alignTo);
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if (show) {
                for (Block b : alignTo) {
                    changeToAlignmentBlock(getPlayer(), b.getLocation());
                }
            } else {
                for (Block b : alignTo) {
                    sendBlockChange(getPlayer(), b);
                }
            }

            alignTo.clear();
        }, 1);
    }

    public void sendBlockChange(Player player, Block b) {
        if (Version.get().isBiggerThan(Version.v1_12)) {
            //block data
            Class<?> blockDataClass = IReflection.getClass(IReflection.ServerPacket.BUKKIT_PACKET, "block.data.BlockData");
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[]{org.bukkit.Location.class, blockDataClass});
            IReflection.MethodAccessor getData = IReflection.getMethod(Block.class, "getBlockData", blockDataClass, new Class[0]);

            sendBlockChange.invoke(player, b.getLocation(), getData.invoke(b));
        } else {
            //loc, mat, byte
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[]{org.bukkit.Location.class, Material.class, byte.class});
            IReflection.MethodAccessor getData = IReflection.getMethod(Block.class, "getData", byte.class, new Class[0]);
            sendBlockChange.invoke(player, b.getLocation(), b.getType(), getData.invoke(b));
        }
    }

    public void changeToAlignmentBlock(Player player, Location loc) {
        if (Version.get().isBiggerThan(Version.v1_12)) {
            //block data
            Class<?> blockDataClass = IReflection.getClass(IReflection.ServerPacket.BUKKIT_PACKET, "block.data.BlockData");
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[]{org.bukkit.Location.class, blockDataClass});
            IReflection.MethodAccessor createBlockData = IReflection.getMethod(Material.class, "createBlockData", blockDataClass, new Class[0]);

            sendBlockChange.invoke(player, loc, createBlockData.invoke(XMaterial.GLASS.parseMaterial()));
        } else {
            //loc, mat, byte
            IReflection.MethodAccessor sendBlockChange = IReflection.getMethod(Player.class, "sendBlockChange", null, new Class[]{org.bukkit.Location.class, Material.class, byte.class});
            sendBlockChange.invoke(player, loc, XMaterial.GLASS.parseMaterial(), (byte) 1);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Portal getPortal() {
        return portal;
    }

    public List<Block> getAlignTo() {
        return alignTo;
    }

    public CustomPortalBlockTool getCustomPortalBlockTool() {
        return customPortalBlockTool;
    }
}
