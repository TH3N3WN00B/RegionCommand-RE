package fr.klemms.regioncommand.events;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.RegionCommand;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.Variable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.ComponentLike;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PluginListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update notifications can be handled here in the future
    }

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event) {
        for (Region region : RegionCommand.commandForRegion) {
            if (region.isRemoved()) continue;
            if (!event.getRegionName().equalsIgnoreCase(region.getRegionName())) continue;
            if (region.getEventType() != EventType.ENTER) continue;

            String command = Variable.replaceVariable(region, event.getPlayer(), region.getCommand());
            Bukkit.getScheduler().runTask(RegionCommand.instance, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            );
        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        for (Region region : RegionCommand.commandForRegion) {
            if (region.isRemoved()) continue;
            if (!event.getRegionName().equalsIgnoreCase(region.getRegionName())) continue;
            if (region.getEventType() != EventType.LEAVE) continue;

            String command = Variable.replaceVariable(region, event.getPlayer(), region.getCommand());
            Bukkit.getScheduler().runTask(RegionCommand.instance, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
            );
        }
    }
}
