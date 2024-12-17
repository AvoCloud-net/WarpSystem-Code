package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.particles.animations.movables.LocationMid;
import de.codingair.codingapi.tools.HitBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PortalMap implements Set<Portal> {
    private final Map<String, Map<Integer, Map<Integer, Set<Portal>>>> portals = new HashMap<>();
    private Set<Portal> cached = new HashSet<>();

    private interface ChunkVisitor {
        boolean visit(@NotNull World world, int x, int z);
    }

    @NotNull
    private @Unmodifiable Set<Portal> getPortals() {
        if (cached != null) return cached;

        Set<Portal> aggregated = new HashSet<>();

        for (Map<Integer, Map<Integer, Set<Portal>>> chunksX : portals.values()) {
            for (Map<Integer, Set<Portal>> chunksZ : chunksX.values()) {
                for (Set<Portal> portals : chunksZ.values()) {
                    aggregated.addAll(portals);
                }
            }
        }

        return cached = Collections.unmodifiableSet(aggregated);
    }

    @Override
    public int size() {
        return getPortals().size();
    }

    @Override
    public boolean isEmpty() {
        return portals.isEmpty();
    }

    @Override
    public boolean contains(Object portal) {
        if (!(portal instanceof Portal)) return false;

        return visit((Portal) portal, ((w, x, z) -> {
            Map<Integer, Map<Integer, Set<Portal>>> chunks = portals.get(w.getName());
            if (chunks == null) return false;

            Map<Integer, Set<Portal>> chunksZ = chunks.get(x);
            if (chunksZ == null) return false;
            Set<Portal> portals = chunksZ.get(z);
            if (portals == null) return false;

            return portals.contains(portal);
        }));
    }

    @Override
    public @NotNull Iterator<Portal> iterator() {
        return getPortals().iterator();
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return getPortals().toArray();
    }

    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return getPortals().toArray(a);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return getPortals().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Portal> c) {
        boolean changed = false;
        for (Portal portal : c) {
            changed |= add(portal);
        }
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for (Object portal : c) {
            changed |= remove(portal);
        }
        return changed;
    }

    @Override
    public void clear() {
        this.portals.clear();
    }

    @NotNull
    public @Unmodifiable Set<Portal> at(@NotNull Location location) {
        if (location.getWorld() == null) return Collections.emptySet();
        int x = location.getBlockX() >> 4, z = location.getBlockZ() >> 4;

        Map<Integer, Map<Integer, Set<Portal>>> chunks = portals.get(location.getWorld().getName());
        if (chunks == null) return Collections.emptySet();

        Map<Integer, Set<Portal>> chunksZ = chunks.get(x);
        if (chunksZ == null) return Collections.emptySet();

        Set<Portal> portals = chunksZ.get(z);
        if (portals == null) return Collections.emptySet();
        return Collections.unmodifiableSet(portals);
    }

    private boolean visit(@NotNull Portal portal, @NotNull ChunkVisitor visitor) {
        Set<Location> points = new HashSet<>();
        for (PortalBlock block : portal.getBlocks()) {
            points.add(block.getLocation());
        }

        for (Animation animation : portal.getAnimations()) {
            World w = animation.getLocation().getWorld();

            HitBox box;
            try {
                box = animation.getEffect().build(null, new LocationMid(animation.getLocation())).getHitBox();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                     InstantiationException e) {
                // ignore
                continue;
            }

            points.add(new Location(w, box.getlX(), box.getlY(), box.getlZ()));
            points.add(new Location(w, box.gethX(), box.gethY(), box.gethZ()));
        }

        World w = points.stream().findAny().map(Location::getWorld).orElse(null);
        if (w == null) {
            // we didn't get any location -> ignore
            return false;
        }

        Set<int[]> chunks = points.stream()
                .map(l -> new int[]{l.getBlockX() >> 4, l.getBlockZ() >> 4})
                .collect(Collectors.toSet());

        boolean change = false;
        for (int[] chunk : chunks) {
            int x = chunk[0], z = chunk[1];
            change |= visitor.visit(w, x, z);
        }
        return change;
    }

    @Override
    public boolean add(Portal portal) {
        cached = null;
        return visit(portal, (w, x, z) -> {
            Map<Integer, Map<Integer, Set<Portal>>> chunks = portals.computeIfAbsent(w.getName(), s -> new HashMap<>());
            return chunks.computeIfAbsent(x, s -> new HashMap<>())
                    .computeIfAbsent(z, s -> new HashSet<>())
                    .add(portal);
        });
    }

    @Override
    public boolean remove(Object portal) {
        cached = null;
        if (!(portal instanceof Portal)) return false;

        return visit((Portal) portal, (w, x, z) -> {
            Map<Integer, Map<Integer, Set<Portal>>> chunks = portals.get(w.getName());
            if (chunks == null) return false;

            Map<Integer, Set<Portal>> chunksZ = chunks.get(x);
            if (chunksZ == null) return false;
            Set<Portal> portals = chunksZ.get(z);
            if (portals == null) return false;

            boolean success = chunks.computeIfAbsent(x, s -> new HashMap<>())
                    .computeIfAbsent(z, s -> new HashSet<>())
                    .remove(portal);

            if (portals.isEmpty()) chunksZ.remove(z);
            if (chunksZ.isEmpty()) chunks.remove(x);
            if (chunks.isEmpty()) this.portals.remove(w.getName());

            return success;
        });
    }
}
