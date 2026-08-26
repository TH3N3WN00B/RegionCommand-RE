package fr.klemms.regioncommand;

import org.bukkit.entity.Player;

public class Variable {

    public static String replaceVariable(Region region, Player player, String command) {
        command = command.replace("$player", player.getName());
        command = command.replace("$uuid", player.getUniqueId().toString());
        command = command.replace("$region", region.getRegionName());
        return command;
    }
}
