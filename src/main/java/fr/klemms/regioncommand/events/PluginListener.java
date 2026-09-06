package fr.klemms.regioncommand.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.PluginLogger;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import fr.klemms.regioncommand.Variable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PluginListener implements Listener {

    private static final long MIN_MOVE_DISTANCE_SQ = 1L;

    private final Map<UUID, Set<String>> playerRegions = new ConcurrentHashMap<>();
    private final Map<String, List<Region>> regionIndex = new HashMap<>();
    private RegionContainer container;

    private RegionManager getManager(Location location) {
        if (container == null) {
            container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            if (container == null) return null;
        }
        return container.get(BukkitAdapter.adapt(location.getWorld()));
    }

    public void indexRegions() {
        regionIndex.clear();
        for (Region region : RegionCommand.commandForRegion) {
            String key = region.getRegionName().toLowerCase();
            regionIndex.computeIfAbsent(key, k -> new ArrayList<>()).add(region);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        if (from.getWorld() == to.getWorld() && from.distanceSquared(to) < MIN_MOVE_DISTANCE_SQ) {
            return;
        }

        updatePlayerRegions(event.getPlayer(), to);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) return;
        updatePlayerRegions(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerRegions.put(player.getUniqueId(), getRegionsAt(player.getLocation()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRegions.remove(event.getPlayer().getUniqueId());
    }

    private void updatePlayerRegions(Player player, Location location) {
        Set<String> previous = playerRegions.getOrDefault(player.getUniqueId(), Collections.emptySet());
        Set<String> current = getRegionsAt(location);

        Set<String> entered = new HashSet<>(current);
        entered.removeAll(previous);

        Set<String> left = new HashSet<>(previous);
        left.removeAll(current);

        playerRegions.put(player.getUniqueId(), current);

        for (String regionName : entered) {
            executeRegionCommands(player, regionName, EventType.ENTER);
        }

        for (String regionName : left) {
            executeRegionCommands(player, regionName, EventType.LEAVE);
        }
    }

    private Set<String> getRegionsAt(Location location) {
        Set<String> result = new HashSet<>();

        try {
            RegionManager manager = getManager(location);
            if (manager == null) return result;

            BlockVector3 point = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            com.sk89q.worldguard.protection.ApplicableRegionSet regions = manager.getApplicableRegions(point);

            for (com.sk89q.worldguard.protection.regions.ProtectedRegion protectedRegion : regions) {
                result.add(protectedRegion.getId());
            }
        } catch (Exception e) {
            RegionCommand.instance.getLogger().log(Level.WARNING, "Failed to query WorldGuard regions at " + location, e);
        }

        return result;
    }

    private void executeRegionCommands(Player player, String regionName, EventType eventType) {
        List<Region> candidates = regionIndex.get(regionName.toLowerCase());
        if (candidates == null || candidates.isEmpty()) return;

        List<String> commands = new ArrayList<>();

        for (Region region : candidates) {
            if (region.getEventType() != eventType) continue;
            commands.add(Variable.replaceVariable(region, player, region.getCommand()));
        }

        if (commands.isEmpty()) return;

        for (String command : commands) {
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception e) {
                RegionCommand.instance.getLogger().log(Level.SEVERE, "Failed to execute command: " + command, e);
                PluginLogger.logError(RegionCommand.instance, "Command execution (command: " + command + ", region: " + regionName + ", player: " + player.getName() + ")", e);
            }
        }
    }
}
