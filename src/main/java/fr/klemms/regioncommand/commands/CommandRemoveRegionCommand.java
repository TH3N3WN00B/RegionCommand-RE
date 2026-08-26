package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Iterator;

public class CommandRemoveRegionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /removeregioncommand <command_id>");
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Command ID must be a number");
            return false;
        }

        Iterator<Region> regionIterator = RegionCommand.commandForRegion.iterator();
        while (regionIterator.hasNext()) {
            Region region = regionIterator.next();
            if (region.getId() == id) {
                regionIterator.remove();
                RegionCommand.saveToDisk();
                sender.sendMessage(ChatColor.GREEN + "Command #" + id + " has been removed.");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Couldn't find a command with ID #" + id);
        return false;
    }
}
