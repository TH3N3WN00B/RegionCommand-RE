package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.EventType;
import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandChangeRegionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(Component.text("Usage: /changeregioncommand <command_id> <region_name> <enter/leave> <command>", NamedTextColor.RED));
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Command ID must be a number", NamedTextColor.RED));
            return false;
        }

        String regionName = args[1];
        String eventTypeStr = args[2];

        if (!eventTypeStr.equalsIgnoreCase("enter") && !eventTypeStr.equalsIgnoreCase("leave")) {
            sender.sendMessage(Component.text("Event type must be 'enter' or 'leave'", NamedTextColor.RED));
            return false;
        }

        String cmd = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        for (Region region : RegionCommand.commandForRegion) {
            if (region.getId() == id) {
                region.setRegionName(regionName);
                region.setEventType(EventType.getEventTypeByName(eventTypeStr));
                region.setCommand(cmd);
                RegionCommand.saveToDisk();
                sender.sendMessage(Component.text("Command #" + id + " has been updated.", NamedTextColor.GREEN));
                return true;
            }
        }

        sender.sendMessage(Component.text("Couldn't find a command with ID #" + id, NamedTextColor.RED));
        return false;
    }
}
