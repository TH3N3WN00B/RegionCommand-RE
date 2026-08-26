package fr.klemms.regioncommand;

import org.bukkit.entity.Player;

public class Variable {

    public static String replaceVariable(Region region, Player player, String command) {
        command = command.replaceAll("\\$player", player.getName());
        command = command.replaceAll("\\$uuid", player.getUniqueId().toString());
        command = command.replaceAll("\\$region", region.getRegionName());
        return command;
    }
}
