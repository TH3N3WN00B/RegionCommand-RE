package fr.klemms.regioncommand.events;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import fr.klemms.regioncommand.Variable;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class PluginListener implements Listener {

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event) {
        executeRegionCommands(event.getPlayer(), event.getRegionName(), EventType.ENTER);
    }

    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        executeRegionCommands(event.getPlayer(), event.getRegionName(), EventType.LEAVE);
    }

    private void executeRegionCommands(Player player, String regionName, EventType eventType) {
        for (Region region : RegionCommand.commandForRegion) {
            if (region.isRemoved()) continue;
            if (!regionName.equalsIgnoreCase(region.getRegionName())) continue;
            if (region.getEventType() != eventType) continue;

            String command = Variable.replaceVariable(region, player, region.getCommand());
            Bukkit.getScheduler().runTask(RegionCommand.instance, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            );
        }
    }
}
