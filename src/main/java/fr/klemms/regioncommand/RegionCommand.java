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

    private UpdateChecker updateChecker;

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

        updateChecker = new UpdateChecker(this);
        AutoUpdater autoUpdater = new AutoUpdater(this, updateChecker);
        getCommand("regioncommandupdate").setExecutor(autoUpdater);
        getServer().getPluginManager().registerEvents(updateChecker, this);
        updateChecker.checkForUpdates();

        getLogger().info("RegionCommand v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        saveToDisk();
        getLogger().info("RegionCommand disabled.");
    }

    public static void saveToDisk() {
        long count = commandForRegion.stream().filter(r -> !r.isRemoved()).count();
        instance.getConfig().set("regionsN", count);
        instance.getConfig().set("regions", null);

        int index = 0;
        for (Region region : commandForRegion) {
            if (region.isRemoved()) continue;
            instance.getConfig().set("regions." + index + ".regionName", region.getRegionName());
            instance.getConfig().set("regions." + index + ".eventType", region.getEventType().getEventName());
            instance.getConfig().set("regions." + index + ".command", region.getCommand());
            index++;
        }

        instance.saveConfig();
    }
}
