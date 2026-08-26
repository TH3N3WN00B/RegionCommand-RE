package fr.klemms.regioncommand.events;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.PluginLogger;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import fr.klemms.regioncommand.Variable;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PluginListener implements Listener {

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event) {
        try {
            executeRegionCommands(event.getPlayer(), event.getRegionName(), EventType.ENTER);
        } catch (Exception e) {
            RegionCommand.instance.getLogger().log(Level.SEVERE, "Error executing region enter commands for region: " + event.getRegionName(), e);
            PluginLogger.logError(RegionCommand.instance, "Region enter event (region: " + event.getRegionName() + ", player: " + event.getPlayer().getName() + ")", e);
        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        try {
            executeRegionCommands(event.getPlayer(), event.getRegionName(), EventType.LEAVE);
        } catch (Exception e) {
            RegionCommand.instance.getLogger().log(Level.SEVERE, "Error executing region leave commands for region: " + event.getRegionName(), e);
            PluginLogger.logError(RegionCommand.instance, "Region leave event (region: " + event.getRegionName() + ", player: " + event.getPlayer().getName() + ")", e);
        }
    }

    private void executeRegionCommands(Player player, String regionName, EventType eventType) {
        List<String> commands = new ArrayList<>();

        for (Region region : RegionCommand.commandForRegion) {
            if (region.isRemoved()) continue;
            if (!regionName.equalsIgnoreCase(region.getRegionName())) continue;
            if (region.getEventType() != eventType) continue;
            commands.add(Variable.replaceVariable(region, player, region.getCommand()));
        }

        if (commands.isEmpty()) return;

        Bukkit.getScheduler().runTask(RegionCommand.instance, () -> {
            for (String command : commands) {
                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                } catch (Exception e) {
                    RegionCommand.instance.getLogger().log(Level.SEVERE, "Failed to execute command: " + command, e);
                    PluginLogger.logError(RegionCommand.instance, "Command execution (command: " + command + ", region: " + regionName + ", player: " + player.getName() + ")", e);
                }
            }
        });
    }
}
