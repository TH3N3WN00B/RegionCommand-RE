package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRemoveRegionCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /removeregioncommand <command_id>", NamedTextColor.RED));
            return false;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Command ID must be a number", NamedTextColor.RED));
            return false;
        }

        Iterator<Region> regionIterator = RegionCommand.commandForRegion.iterator();
        while (regionIterator.hasNext()) {
            Region region = regionIterator.next();
            if (region.getId() == id) {
                regionIterator.remove();
                RegionCommand.saveToDisk();
                RegionCommand.instance.reindexRegions();
                sender.sendMessage(Component.text("Command #" + id + " has been removed.", NamedTextColor.GREEN));
                return true;
            }
        }

        sender.sendMessage(Component.text("Couldn't find a command with ID #" + id, NamedTextColor.RED));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return RegionCommand.commandForRegion.stream()
                    .map(r -> String.valueOf(r.getId()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
