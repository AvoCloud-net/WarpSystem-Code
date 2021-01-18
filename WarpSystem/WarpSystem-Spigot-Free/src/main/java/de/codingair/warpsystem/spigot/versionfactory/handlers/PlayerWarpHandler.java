package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.tools.io.ConfigMask;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ImprovedDouble;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.PlayerWarpData;
import de.codingair.warpsystem.spigot.api.StringFormatter;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarpReference;
import de.codingair.warpsystem.spigot.features.playerwarps.commands.CPlayerWarps;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.PWList;
import de.codingair.warpsystem.spigot.features.playerwarps.listeners.PlayerWarpListener;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.Category;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.tempwarps.TempWarpAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerWarpHandler extends PlayerWarpManager {
    private boolean hideLimitInfo = false;

    public PlayerWarpHandler() {
        super();
        listener = new Listener();
    }

    @Override
    public boolean hasPermission(Player player) {
        int warps = PlayerWarpManager.getManager().getOwnWarps(player).size();
        int maxAmount = getMaxAmount(player);

        return warps < maxAmount;
    }

    @Override
    public int getMaxAmount(Player player) {
        if (player.isOp()) return 3;

        if (Permissions.PERMISSION_USE_PLAYER_WARPS != null) {
            int amount = 0;
            for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
                if (!effectivePermission.getValue()) continue;
                String perm = effectivePermission.getPermission();

                if (perm.equals("*") || perm.equalsIgnoreCase("warpsystem.*")) return 3;
                if (perm.toLowerCase().startsWith("warpsystem.playerwarps.")) {
                    String s = perm.substring(23);
                    if (s.equals("*") || s.equalsIgnoreCase("n")) return 3;

                    try {
                        int i = Math.min(Integer.parseInt(s), 3);
                        if (i > amount) amount = i;
                    } catch (Throwable ignored) {
                    }
                }
            }
            return amount;
        } else return Math.min(maxAmount, 3);
    }

    @Override
    public boolean load(boolean loader) {
        this.warps.clear();
        this.warpCategories.clear();
        this.nameBlacklist.clear();
        this.worldBlacklist.clear();

        this.playerWarpsData = WarpSystem.getInstance().getFileManager().loadFile("PlayerWarps", "/Memory/");
        this.config = WarpSystem.getInstance().getFileManager().loadFile("PlayerWarpConfig", "/");
        FileConfiguration config = this.config.getConfig();

        int size = 0;

        this.bungeeCord = false;
        this.economy = true;
        this.forcePlayerHead = true;
        this.customTeleportCosts = true;
        this.classes = false;

        config.set("PlayerWarps.General.BungeeCord", false);
        config.set("PlayerWarps.General.Economy", true);
        config.set("PlayerWarps.General.Name_Blacklist", new ArrayList<>());
        config.set("PlayerWarps.General.Force_Player_Head", true);
        config.set("PlayerWarps.General.Custom_teleport_costs", true);
        config.set("PlayerWarps.General.Categories.Enabled", false);
        config.set("PlayerWarps.General.Categories.Classes", new ArrayList<>());
        this.config.saveConfig();

        WarpSystem.log("  > Loading PlayerWarps [Bungee: " + bungeeCord + "; TimeDependent: " + economy + "]");

        // Timings
        this.minTime = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Time.Min_Time", null), 300000);
        this.maxTime = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Time.Max_Time", null), 2592000000L);

        List<String> reminds = config.getStringList("Inactive.Reminds");
        this.inactiveReminds = new ArrayList<>();

        for (String data : reminds) {
            long time = StringFormatter.convertFromTimeFormat(data);
            if (time > 0) inactiveReminds.add(time);
        }

        this.inactiveTime = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Inactive.Time_After_Expiration", null), 2592000000L);

        this.hideLimitInfo = config.getBoolean("PlayerWarps.Hide_Limit_Info", false);

        //Costs - Generally
        this.maxAmount = config.getInt("PlayerWarps.General.Max_Warp_Amount", 3);
        this.protectedRegions = config.getBoolean("PlayerWarps.General.Support.ProtectedRegions", true);
        this.nameBlacklist.addAll(config.getStringList("PlayerWarps.General.Name_Blacklist"));
        this.worldBlacklist.addAll(config.getStringList("PlayerWarps.General.World_Blacklist"));
        this.createCosts = config.getDouble("PlayerWarps.Costs.Create", 200);
        this.editCosts = config.getDouble("PlayerWarps.Costs.Edit", 200);
        this.naturalNumbers = config.getBoolean("PlayerWarps.Costs.Round_costs_to_natural_numbers", false);
        this.internalRefundFactor = config.getBoolean("PlayerWarps.Costs.Internal_Refund_Factor", false);
        this.forcePlayerHead = config.getBoolean("PlayerWarps.General.Force_Player_Head", false);
        this.customTeleportCosts = config.getBoolean("PlayerWarps.General.Custom_teleport_costs", true);
        this.timeStandardValue = StringFormatter.convertFromTimeFormat(config.getString("PlayerWarps.Time.Standard_Value", "1h"));
        this.forceCreateGUI = config.getBoolean("PlayerWarps.General.Force_Create_GUI", false);
        this.allowPublicWarps = config.getBoolean("PlayerWarps.General.Allow_Public_Warps", true);
        this.allowTrustedMembers = config.getBoolean("PlayerWarps.General.Allow_Trusted_Members", true);
        this.allowTeleportMessage = config.getBoolean("PlayerWarps.General.Allow_Teleport_Messages", true);
        this.allowDescription = config.getBoolean("PlayerWarps.General.Allow_Description", true);
        this.time = true;

        //Costs - Editing
        this.nameChangeCosts = config.getDouble("PlayerWarps.Costs.Editing.Name", 400);
        this.positionChangeCosts = config.getDouble("PlayerWarps.Costs.Editing.Target_Position", 200);
        this.itemChangeCosts = config.getDouble("PlayerWarps.Costs.Editing.Personal_Item", 100);

        //Costs - Fields
        this.personalItemCosts = config.getDouble("PlayerWarps.Costs.Personal_Item", 200);
        this.messageCosts = config.getDouble("PlayerWarps.Costs.Text.Teleport_Message", 2);
        this.descriptionCosts = config.getDouble("PlayerWarps.Costs.Text.Warp_Description", 2);

        this.publicCosts = config.getDouble("PlayerWarps.Costs.PublicWarp", 100);
        this.activeTimeCosts = config.getDouble("PlayerWarps.Costs.Active_Time", 0.5);

        //Teleport costs
        this.teleportCosts = config.getDouble("PlayerWarps.Costs.Teleport_Fee", 25);
        this.maxTeleportCosts = config.getDouble("PlayerWarps.Teleport_Fee.Max", 500);

        //Teleport message
        this.messageMinLength = config.getInt("PlayerWarps.Teleport_Message.Length.Min", 5);
        this.messageMaxLength = config.getInt("PlayerWarps.Teleport_Message.Length.Max", 50);

        //Description
        this.descriptionLineMinLength = config.getInt("PlayerWarps.Warp_Description.Line_Length.Min", 5);
        this.descriptionLineMaxLength = config.getInt("PlayerWarps.Warp_Description.Line_Length.Max", 25);
        this.descriptionMaxLines = config.getInt("PlayerWarps.Warp_Description.Max_Lines", 3);

        //Name
        this.nameMinLength = config.getInt("PlayerWarps.Name_Length.Min", 3);
        this.nameMaxLength = config.getInt("PlayerWarps.Name_Length.Max", 20);

        //generally
        this.firstPublic = config.getBoolean("PlayerWarps.General.Public_as_create_state", false);
        this.trustedMemberCosts = config.getDouble("PlayerWarps.Costs.Trusted_Member", 50);

        //refund
        this.personalItemRefund = config.getDouble("PlayerWarps.Refunds.Personal_Item", 0.5);
        this.descriptionRefund = config.getDouble("PlayerWarps.Refunds.Warp_Description", 0.5);
        this.messageRefund = config.getDouble("PlayerWarps.Refunds.Teleport_Message", 0.5);
        this.publicRefund = config.getDouble("PlayerWarps.Refunds.PublicWarp", 0.5);
        this.teleportCostsRefund = config.getDouble("PlayerWarps.Refunds.Teleport_Fee", 0.5);
        this.activeTimeRefund = config.getDouble("PlayerWarps.Refunds.Active_Time", 1);
        this.trustedMemberRefund = config.getDouble("PlayerWarps.Refunds.Trusted_Member", 0.5);

        //Classes
        this.warpCategories.add(new Category(new ItemBuilder(XMaterial.EMERALD), "§a§lShop", 1, new ArrayList<String>() {{
            add("§7This class marks a warp");
            add("§7as a §aShop§7!");
        }}));

        this.warpCategories.add(new Category(new ItemBuilder(XMaterial.OAK_DOOR), "§c§lHome", 2, new ArrayList<String>() {{
            add("§7This class marks a warp");
            add("§7as a §cHome§7!");
        }}));

        this.warpCategories.add(new Category(new ItemBuilder(XMaterial.FARMLAND), "§9§lFarm", 3, new ArrayList<String>() {{
            add("§7This class marks a warp");
            add("§7as a §9Farm§7!");
        }}));

        this.warpCategories.add(new Category(new ItemBuilder(XMaterial.IRON_SWORD), "§e§lPvP-Zone", 4, new ArrayList<String>() {{
            add("§7This class marks a warp");
            add("§7as a §ePvP-Zone§7!");
        }}));

        this.warpCategories.add(new Category(new ItemBuilder(XMaterial.BOW), "§b§lHunting-Area", 5, new ArrayList<String>() {{
            add("§7This class marks a warp");
            add("§7as a §bHunting-Area§7!");
        }}));

        this.warpCategories.add(new Category(new ItemBuilder(XMaterial.ENDER_EYE), "§3§lMiscellaneous", 6, new ArrayList<String>() {{
            add("§7This class marks a warp");
            add("§7as a §3miscellaneous §7warp!");
        }}));

        //loading PlayerWarps
        List<?> data = playerWarpsData.getConfig().getList("PlayerWarps");
        if (data != null)
            for (Object o : data) {
                JSON json = new JSON((Map<?, ?>) o);
                PlayerWarp p = new PlayerWarp();

                try {
                    p.read(json);
                    add(p);
                    size++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        List<PlayerWarp> imported = TempWarpAdapter.convertTempWarps(true);
        for (PlayerWarp playerWarp : imported) {
            add(playerWarp);
        }

        new CPlayerWarp(config.getStringList("PlayerWarps.General.PlayerWarp_Command_Aliases")).register();
        new CPlayerWarps(config.getStringList("PlayerWarps.General.PlayerWarps_Command_Aliases")).register();

        List<String> aliases = config.getStringList("PlayerWarps.General.Command_References");
        if (!aliases.isEmpty()) new CPlayerWarpReference(aliases.remove(0), aliases.toArray(new String[0])).register();

        WarpSystem.log("    ...got " + warpCategories.size() + " Class(es)");
        if (!imported.isEmpty()) WarpSystem.log("    ...got " + imported.size() + " imported TempWarp(s)");
        imported.clear();

        if (!bungeeCord) WarpSystem.log("    ...got " + size + " PlayerWarp(s)");
        if (economy && time) API.addTicker(this);

        Bukkit.getPluginManager().registerEvents(this.listener, WarpSystem.getInstance());
        return true;
    }

    @Override
    public void save(boolean saver) {
        if (!saver) WarpSystem.log("  > Saving PlayerWarps...");
        playerWarpsData.clearConfig();

        JSONArray a = null;
        if (!bungeeCord || !WarpSystem.getInstance().isOnProxy()) {
            a = new JSONArray();

            for (List<PlayerWarp> data : this.warps.values()) {
                for (PlayerWarp w : data) {
                    JSON json = new JSON();
                    w.write(json);
                    a.add(json);
                }
            }
            playerWarpsData.getConfig().set("PlayerWarps", a);
        } else if (!saver) WarpSystem.log("    ...skipping PlayerWarp(s) > Saved on BungeeCord");

        config.loadConfig();
        ConfigMask writer = new ConfigMask(config);
        writer.put("PlayerWarps.Hide_Limit_Info", hideLimitInfo);
        config.saveConfig();

        playerWarpsData.saveConfig();
        if (!saver && a != null) WarpSystem.log("    ...saved " + a.size() + " PlayerWarp(s)");
    }

    @Override
    public void sync(PlayerWarp old, PlayerWarp warp) {
    }

    @Override
    public void sync(PlayerWarpData old, PlayerWarpData warp) {
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
    }

    public static class Listener extends PlayerWarpListener {
        @EventHandler
        @Override
        public void onJoin(PlayerFinalJoinEvent e) {
            PlayerWarpManager.getManager().checkPlayerWarpOwnerNames(e.getPlayer());
            List<PlayerWarp> notify = new ArrayList<>();

            boolean timeDependent = PlayerWarpManager.getManager().isEconomy();
            double money = 0;
            List<PlayerWarp> warps = PlayerWarpManager.getManager().getOwnWarps(e.getPlayer());
            for (PlayerWarp warp : warps) {
                if (timeDependent && warp.isExpired()) {
                    notify.add(warp);
                }

                money += warp.getInactiveSales() * warp.getTeleportCosts();
            }

            if (money > 0 || !notify.isEmpty() || (e.getPlayer().hasPermission(Permissions.PERMISSION_MODIFY_PLAYER_WARPS) && !((PlayerWarpHandler) PlayerWarpManager.getManager()).hideLimitInfo)) {
                double finalMoney = money;
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                    if (!notify.isEmpty()) {
                        for (PlayerWarp warp : notify) {
                            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("Warp_expiring").replace("%NAME%", warp.getName()).replace("%TIME_LEFT%", StringFormatter.convertInTimeFormat(PlayerWarpManager.getManager().getInactiveTime() - (System.currentTimeMillis() - warp.getExpireDate()), 0, "", "")));
                        }
                        notify.clear();
                    }

                    if (finalMoney > 0) {
                        SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("Warp_Money_Available").replace("%AMOUNT%", new ImprovedDouble(finalMoney).toString()), WarpSystem.getInstance());
                        message.replace("%BUTTON%", new ChatButton(Lang.get("Warp_Money_Available_Button")) {
                            @Override
                            public void onClick(Player player) {
                                new PWList(player).open();
                                message.destroy();
                            }
                        }.setHover(Lang.get("Click_Hover")));

                        message.setTimeOut(60);

                        message.send(e.getPlayer());
                    }

                    Player player = e.getPlayer();
                    if (player.hasPermission(Permissions.PERMISSION_MODIFY_PLAYER_WARPS) && !((PlayerWarpHandler) PlayerWarpManager.getManager()).hideLimitInfo) {
                        SimpleMessage message = new SimpleMessage(Lang.getPrefix() + "§7PlayerWarps are §climited §7to §c3 warps §7per §7player. §8[", WarpSystem.getInstance());

                        TextComponent upgrade = new TextComponent("§6§nPremium");
                        upgrade.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/premium-warps-portals-and-more-warp-teleport-system-1-8-1-14.66035/"));
                        upgrade.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("§8» §6§lClick §8«")}));

                        message.add(upgrade);
                        message.add("§8 | ");

                        message.add(new ChatButton("§7Hide", "§7» Click «") {
                            @Override
                            public void onClick(Player player) {
                                ((PlayerWarpHandler) PlayerWarpManager.getManager()).hideLimitInfo = true;
                                PlayerWarpManager.getManager().save(true);
                                player.sendMessage(Lang.getPrefix() + "§7You won't see this message again.");
                                message.destroy();
                            }
                        });

                        message.setTimeOut(600);

                        message.add("§8]");
                        message.send(player);
                    }
                }, 5 * 20L);
            }
        }
    }
}
