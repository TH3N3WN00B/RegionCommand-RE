package fr.klemms.regioncommand;

import fr.klemms.regioncommand.commands.CommandAddRegionCommand;
import fr.klemms.regioncommand.commands.CommandChangeRegionCommand;
import fr.klemms.regioncommand.commands.CommandRegionCommandList;
import fr.klemms.regioncommand.commands.CommandRemoveRegionCommand;
import fr.klemms.regioncommand.events.PluginListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RegionCommand extends JavaPlugin {

    public static RegionCommand instance;
    public static List<Region> commandForRegion = new ArrayList<>();
    public static int nextCommandID = 0;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getConfig().addDefault("pluginVersion", 2);
        getConfig().options().copyDefaults(true);

        ConfigMigrator.migrate(this);
        Config.readConfig(this);

        getCommand("addregioncommand").setExecutor(new CommandAddRegionCommand());
        getCommand("removeregioncommand").setExecutor(new CommandRemoveRegionCommand());
        getCommand("changeregioncommand").setExecutor(new CommandChangeRegionCommand());
        getCommand("regioncommandlist").setExecutor(new CommandRegionCommandList());

        getServer().getPluginManager().registerEvents(new PluginListener(), this);

        getLogger().info("RegionCommand v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        saveToDisk();
        getLogger().info("RegionCommand disabled.");
    }

    public static void saveToDisk() {
        int num = 0;
        for (Region region : commandForRegion) {
            if (region.isRemoved()) continue;
            num++;
        }

        instance.getConfig().set("regionsN", num);
        instance.getConfig().set("regions", "");

        num = 0;
        for (Region region : commandForRegion) {
            if (region.isRemoved()) continue;
            instance.getConfig().set("regions." + num + ".regionName", region.getRegionName());
            instance.getConfig().set("regions." + num + ".eventType", region.getEventType().getEventName());
            instance.getConfig().set("regions." + num + ".command", region.getCommand());
            num++;
        }

        instance.saveConfig();
    }
}
