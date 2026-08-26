package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAddRegionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /addregioncommand <region_name> <enter/leave> <command>");
            return false;
        }

        String regionName = args[0];
        String eventTypeStr = args[1];

        if (!eventTypeStr.equalsIgnoreCase("enter") && !eventTypeStr.equalsIgnoreCase("leave")) {
            sender.sendMessage(ChatColor.RED + "Event type must be 'enter' or 'leave'");
            return false;
        }

        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            commandBuilder.append(args[i]);
            if (i < args.length - 1) {
                commandBuilder.append(" ");
            }
        }

        EventType eventType = EventType.getEventTypeByName(eventTypeStr);
        Region region = new Region(regionName, eventType, commandBuilder.toString(), RegionCommand.nextCommandID++);
        RegionCommand.commandForRegion.add(region);
        RegionCommand.saveToDisk();

        sender.sendMessage(ChatColor.GREEN + "Command added for region '" + regionName + "' (" + eventTypeStr + "): /" + commandBuilder);
        return true;
    }
}
