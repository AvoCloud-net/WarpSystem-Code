package de.codingair.warpsystem.spigot.features.spawn.utils;

import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.api.destinations.utils.Result;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.spigot.api.placeholders.PAPI;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.EmptyAdapter;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Spawn extends FeatureObject {
    private Usage usage;
    private RespawnUsage respawnUsage;
    //first join actions
    private List<String> broadCastMessages;
    private boolean randomFireWorks;
    private de.codingair.codingapi.tools.Location firstJoin;

    private String displayName;

    public Spawn() {
        usage = Usage.LOCAL;
        broadCastMessages = new ArrayList<>();
        randomFireWorks = false;
        respawnUsage = RespawnUsage.DISABLED;
        displayName = "Spawn";
        firstJoin = null;
    }

    public static String prepareBroadcastMessage(String s, Player player) {
        return PAPI.convert(ChatColor.translateAll('&', s), player).replace("\\n", "\n").replace("%player%", player.getName());
    }

    public boolean isValid() {
        Location l;
        return (hasAction(Action.WARP) && (l = getLocation()) != null && l.getWorld() != null) || switchServer();
    }

    public Location getLocation() {
        if (!hasAction(Action.WARP)) return null;

        return getAction(WarpAction.class).getValue().buildLocation();
    }

    public boolean switchServer() {
        return SpawnManager.getInstance().getSpawnServerCommand() != null && !SpawnManager.getInstance().getSpawnServerCommand().equals(WarpSystem.getInstance().getCurrentServer());
    }

    public Spawn clone() {
        Spawn clone = new Spawn();
        clone.apply(this);
        return clone;
    }

    @Override
    public void apply(FeatureObject object) {
        super.apply(object);

        if (object instanceof Spawn) {
            Spawn other = (Spawn) object;
            this.usage = other.usage;
            this.broadCastMessages.clear();
            this.broadCastMessages.addAll(other.broadCastMessages);
            this.randomFireWorks = other.randomFireWorks;
            this.respawnUsage = other.respawnUsage;
            this.displayName = other.displayName;
            this.firstJoin = other.firstJoin == null ? null : other.firstJoin.clone();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Spawn spawn = (Spawn) o;
        return usage == spawn.usage &&
                respawnUsage == spawn.respawnUsage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(usage, respawnUsage);
    }

    @Override
    public void prepareTeleportOptions(String player, TeleportOptions options) {
        super.prepareTeleportOptions(player, options);
        options.setDisplayName(displayName);
    }

    public FeatureObject teleportToFirstJoin(Player player) {
        if (this.firstJoin == null || this.firstJoin.getWorld() == null) return this;

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(this.firstJoin)), displayName);
        options.setSkip(true);

        return super.perform(player, options);
    }

    public void onJoin(PlayerSpawnLocationEvent e, boolean firstJoin) {
        Location l = getLocation();
        if (l != null && l.getWorld() != null) {
            e.setSpawnLocation(l);
            if (firstJoin) firstJoin(e);
        }
    }

    private void firstJoin(PlayerSpawnLocationEvent e) {
        if (firstJoin != null && firstJoin.getWorld() != null) e.setSpawnLocation(firstJoin);
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            spawnFireWorks(e.getSpawnLocation());
            broadcast(e.getPlayer());
        }, 1L);
    }

    @NotNull
    private EntityType fireworkRocketType() {
        try {
            return EntityType.FIREWORK_ROCKET;
        } catch (Exception ex) {
            return EntityType.valueOf("FIREWORK");
        }
    }

    private void spawnFireWorks(Location l) {
        if (randomFireWorks) {
            if (l != null) {
                Random r = new Random();
                FireworkEffect.Type type = FireworkEffect.Type.BALL;

                int r1i = r.nextInt(17) + 1;
                int r2i = r.nextInt(17) + 1;
                Color c1 = Environment.getColor(r1i);
                Color c2 = Environment.getColor(r2i);

                FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

                Firework fw;

                try {
                    fw = (Firework) l.getWorld().spawnEntity(l, fireworkRocketType());
                } catch (Exception ex) {
                    return;
                }

                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.setPower(1);

                fwm.addEffect(effect);

                fw.setFireworkMeta(fwm);
            }
        }
    }

    private void broadcast(Player player) {
        if (this.broadCastMessages != null && !this.broadCastMessages.isEmpty()) {
            for (String s : this.broadCastMessages) {
                Bukkit.broadcastMessage(prepareBroadcastMessage(s, player));
            }
        }
    }

    @Override
    public FeatureObject perform(Player player, TeleportOptions options) {
        if (switchServer()) {
            //switch
            options.setDestination(new Destination(new EmptyAdapter()));
            options.setMessage(null);
            options.addCallback(new Callback<Result>() {
                @Override
                public void accept(Result result) {
                    if (result == Result.SUCCESS) {
                        WarpSystem.getDataHandler().send(new TeleportSpawnPacket(player.getName(), false), player);
                    }
                }
            });
        }

        return super.perform(player, options);
    }

    @Override
    public boolean read(DataMask d) throws Exception {
        boolean success = super.read(d);

        Object usage = d.get("usage");
        if (usage instanceof Integer) this.usage = Usage.getByLegacyId((Integer) usage);
        else if (usage instanceof String) this.usage = Usage.getByName((String) usage);
        else this.usage = Usage.getDefault();
        
        Object respawnUsage = d.get("respawn");
        if (respawnUsage instanceof Integer) this.respawnUsage = RespawnUsage.getByLegacyId((Integer) respawnUsage);
        else if (respawnUsage instanceof String) this.respawnUsage = RespawnUsage.getByName((String) respawnUsage);
        else this.respawnUsage = RespawnUsage.getDefault();

        this.randomFireWorks = d.getBoolean("fireworks", false);
        this.broadCastMessages = d.getList("broadcast");
        this.displayName = d.getString("displayname", "Spawn");
        this.firstJoin = d.getLocation("firstjoin");

        return success;
    }

    @Override
    public void write(DataMask d) {
        super.write(d);

        d.put("usage", this.usage.name());
        d.put("respawn", this.respawnUsage.name());
        d.put("fireworks", this.randomFireWorks);
        d.put("broadcast", this.broadCastMessages);
        d.put("displayname", this.displayName);
        d.put("firstjoin", this.firstJoin);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public List<String> getBroadCastMessages() {
        return broadCastMessages;
    }

    public void setBroadCastMessages(List<String> broadCastMessages) {
        if (broadCastMessages == null) {
            this.broadCastMessages.clear();
            return;
        }

        this.broadCastMessages = broadCastMessages;
    }

    public boolean isRandomFireWorks() {
        return randomFireWorks;
    }

    public void setRandomFireWorks(boolean randomFireWorks) {
        this.randomFireWorks = randomFireWorks;
    }

    public RespawnUsage getRespawnUsage() {
        return respawnUsage;
    }

    public void setRespawnUsage(RespawnUsage respawnUsage) {
        this.respawnUsage = respawnUsage;
    }

    public de.codingair.codingapi.tools.Location getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(Location firstJoin) {
        this.firstJoin = new de.codingair.codingapi.tools.Location(firstJoin);
    }

    /**
     * DO NOT RENAME VALUES (important for serialization)
     */
    public enum Usage {
        LOCAL(0, "§b" + Lang.get("Local") + " §7(/spawn)"),
        LOCAL_FIRST_JOIN(1, "§b" + Lang.get("Local") + " §7(/spawn) §8+ " + "§7" + Lang.get("First_Join")),
        LOCAL_EVERY_JOIN(2, "§b" + Lang.get("Local") + " §7(/spawn) §8+ " + "§7" + Lang.get("Every_Join")),
        GLOBAL(3, "§e" + Lang.get("Global") + " §7(/spawn)", true),
        GLOBAL_FIRST_JOIN(4, "§e" + Lang.get("Global") + " §7(/spawn) §8+ " + "§7" + Lang.get("First_Join"), true),
        GLOBAL_EVERY_JOIN(5, "§e" + Lang.get("Global") + " §7(/spawn) §8+ " + "§7" + Lang.get("Every_Join"), true),
        GLOBAL_EVERY_PROXY_JOIN(0, "§e" + Lang.get("Global") + " §7(/spawn) §8+ " + "§7" + Lang.get("Every_Proxy_Join"), true),
        FIRST_JOIN(7, "§7" + Lang.get("First_Join")),
        EVERY_JOIN(6, "§7" + Lang.get("Every_Join")),
        DISABLED(8, "§c" + Lang.get("Disabled"));

        private final int legacyId;
        private final String display;
        private final boolean bungee;

        Usage(int legacyId, String display, boolean bungee) {
            this.legacyId = legacyId;
            this.display = display;
            this.bungee = bungee;
        }

        Usage(int legacyId, String display) {
            this(legacyId, display, false);
        }

        public static @NotNull Usage getByLegacyId(int legacyId) {
            for (Usage value : values()) {
                if (value.legacyId == legacyId) return value;
            }
            
            return getDefault();
        }

        public static @NotNull Usage getByName(String name) {
            for (Usage value : values()) {
                if (value.name().equals(name)) return value;
            }
            
            return getDefault();
        }

        @NotNull
        public static Usage getDefault() {
            return LOCAL;
        }

        public Usage next() {
            int next = ordinal() + 1;
            if (next == values().length) next = 0;
            return values()[next];
        }

        public Usage getWithoutSpawnCommand() {
            switch (this) {
                case LOCAL:
                case GLOBAL:
                    return DISABLED;

                case LOCAL_FIRST_JOIN:
                case GLOBAL_FIRST_JOIN:
                    return FIRST_JOIN;

                case LOCAL_EVERY_JOIN:
                case GLOBAL_EVERY_JOIN:
                case GLOBAL_EVERY_PROXY_JOIN:
                    return EVERY_JOIN;

                default:
                    return this;
            }
        }

        public Usage previous() {
            int previous = ordinal() - 1;
            if (previous < 0) previous = values().length - 1;
            return values()[previous];
        }

        public Usage getLocal() {
            if (this == GLOBAL_EVERY_PROXY_JOIN) return LOCAL_EVERY_JOIN;
            return valueOf(name().replace("GLOBAL", "LOCAL"));
        }

        public String getDisplay() {
            return display;
        }

        public boolean isBungee() {
            return bungee;
        }
    }

    /**
     * DO NOT RENAME VALUES (important for serialization)
     */
    public enum RespawnUsage {
        DISABLED(0, "§c" + Lang.get("Disabled")),
        LOCAL(1, "§b" + Lang.get("Local")),
        GLOBAL(2, "§e" + Lang.get("Global"), true);

        private final int legacyId;
        private final String display;
        private final boolean bungee;

        RespawnUsage(int legacyId, String display, boolean bungee) {
            this.legacyId = legacyId;
            this.display = display;
            this.bungee = bungee;
        }

        RespawnUsage(int legacyId, String display) {
            this(legacyId, display, false);
        }

        public static @NotNull RespawnUsage getByLegacyId(int legacyId) {
            for (RespawnUsage value : values()) {
                if (value.legacyId == legacyId) return value;
            }
            
            return getDefault();
        }

        public static @NotNull RespawnUsage getByName(String name) {
            for (RespawnUsage value : values()) {
                if (value.name().equals(name)) return value;
            }
            
            return getDefault();
        }

        @NotNull
        public static RespawnUsage getDefault() {
            return DISABLED;
        }

        public RespawnUsage next() {
            int next = ordinal() + 1;
            if (next == values().length) next = 0;
            return values()[next];
        }

        public RespawnUsage getLocal() {
            return valueOf(name().replace("GLOBAL", "LOCAL"));
        }

        public RespawnUsage previous() {
            int previous = ordinal() - 1;
            if (previous < 0) previous = values().length - 1;
            return values()[previous];
        }

        public String getDisplay() {
            return display;
        }

        public boolean isBungee() {
            return bungee;
        }
    }
}
