package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandChangeRegionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /changeregioncommand <command_id> <region_name> <enter/leave> <command>");
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Command ID must be a number");
            return false;
        }

        String regionName = args[1];
        String eventTypeStr = args[2];

        if (!eventTypeStr.equalsIgnoreCase("enter") && !eventTypeStr.equalsIgnoreCase("leave")) {
            sender.sendMessage(ChatColor.RED + "Event type must be 'enter' or 'leave'");
            return false;
        }

        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            commandBuilder.append(args[i]);
            if (i < args.length - 1) {
                commandBuilder.append(" ");
            }
        }

        for (Region region : RegionCommand.commandForRegion) {
            if (region.getId() == id) {
                region.setRegionName(regionName);
                region.setEventType(EventType.getEventTypeByName(eventTypeStr));
                region.setCommand(commandBuilder.toString());
                RegionCommand.saveToDisk();
                sender.sendMessage(ChatColor.GREEN + "Command #" + id + " has been updated.");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Couldn't find a command with ID #" + id);
        return false;
    }
}
