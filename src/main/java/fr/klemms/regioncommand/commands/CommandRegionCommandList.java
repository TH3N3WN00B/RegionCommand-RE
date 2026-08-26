package fr.klemms.regioncommand.commands;

import fr.klemms.regioncommand.Region;
import fr.klemms.regioncommand.RegionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegionCommandList implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        int count = (int) RegionCommand.commandForRegion.stream().filter(r -> !r.isRemoved()).count();

        player.sendMessage(Component.text("|---- RegionCommand -- " + count + " commands ----", NamedTextColor.GOLD));

        for (Region region : RegionCommand.commandForRegion) {
            if (region.isRemoved()) continue;

            player.sendMessage(Component.text("| Command ID: ", NamedTextColor.GOLD)
                    .append(Component.text(region.getId(), NamedTextColor.LIGHT_PURPLE)));

            player.sendMessage(Component.text("| Command: ", NamedTextColor.GOLD)
                    .append(Component.text("/" + region.getCommand(), NamedTextColor.LIGHT_PURPLE)));

            Component regionInfo = Component.text("| Region: ", NamedTextColor.GOLD)
                    .append(Component.text(region.getRegionName(), NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" - Type: ", NamedTextColor.GOLD))
                    .append(Component.text(region.getEventType().getEventName(), NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" ", NamedTextColor.GOLD));

            Component removeButton = Component.text("[Remove]", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                    .clickEvent(ClickEvent.runCommand("/removeregioncommand " + region.getId()))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to remove this command", NamedTextColor.GOLD)));

            Component changeButton = Component.text("[Change]", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                    .clickEvent(ClickEvent.suggestCommand("/changeregioncommand " + region.getId() + " " + region.getRegionName() + " " + region.getEventType().getEventName() + " " + region.getCommand()))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to edit this command", NamedTextColor.GOLD)));

            player.sendMessage(regionInfo.append(removeButton).append(changeButton));
            player.sendMessage(Component.text("|---------------", NamedTextColor.GOLD));
        }

        return true;
    }
}
