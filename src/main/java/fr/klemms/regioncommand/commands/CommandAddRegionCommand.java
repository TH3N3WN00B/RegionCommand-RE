package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAddRegionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /addregioncommand <region_name> <enter/leave> <command>", NamedTextColor.RED));
            return false;
        }

        String regionName = args[0];
        String eventTypeStr = args[1];

        if (!eventTypeStr.equalsIgnoreCase("enter") && !eventTypeStr.equalsIgnoreCase("leave")) {
            sender.sendMessage(Component.text("Event type must be 'enter' or 'leave'", NamedTextColor.RED));
            return false;
        }

        String cmd = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
        EventType eventType = EventType.getEventTypeByName(eventTypeStr);
        Region region = new Region(regionName, eventType, cmd, RegionCommand.nextCommandID++);
        RegionCommand.commandForRegion.add(region);
        RegionCommand.saveToDisk();

        sender.sendMessage(Component.text("Command added for region '" + regionName + "' (" + eventTypeStr + "): /" + cmd, NamedTextColor.GREEN));
        return true;
    }
}
