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

    private PluginListener pluginListener;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        try {
            ConfigUpdater.update(this);
            Config.readConfig(this);

            CommandAddRegionCommand addCommand = new CommandAddRegionCommand();
            CommandRemoveRegionCommand removeCommand = new CommandRemoveRegionCommand();
            CommandChangeRegionCommand changeCommand = new CommandChangeRegionCommand();

            getCommand("addregioncommand").setExecutor(addCommand);
            getCommand("addregioncommand").setTabCompleter(addCommand);
            getCommand("removeregioncommand").setExecutor(removeCommand);
            getCommand("removeregioncommand").setTabCompleter(removeCommand);
            getCommand("changeregioncommand").setExecutor(changeCommand);
            getCommand("changeregioncommand").setTabCompleter(changeCommand);
            getCommand("regioncommandlist").setExecutor(new CommandRegionCommandList());

            pluginListener = new PluginListener();
            pluginListener.indexRegions();
            getServer().getPluginManager().registerEvents(pluginListener, this);

            updateChecker = new UpdateChecker(this);
            AutoUpdater autoUpdater = new AutoUpdater(this, updateChecker);
            getCommand("regioncommandupdate").setExecutor(autoUpdater);
            getServer().getPluginManager().registerEvents(updateChecker, this);
            updateChecker.checkForUpdates();

            getLogger().info("RegionCommand v" + getDescription().getVersion() + " enabled!");
        } catch (Exception e) {
            getLogger().severe("RegionCommand failed to enable! Check logs/ folder for crash report.");
            PluginLogger.logCrash(this, e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        saveToDisk();
        getLogger().info("RegionCommand disabled.");
    }

    public void reindexRegions() {
        if (pluginListener != null) {
            pluginListener.indexRegions();
        }
    }

    public static void saveToDisk() {
        instance.getConfig().set("regionsN", commandForRegion.size());
        instance.getConfig().set("regions", null);

        int index = 0;
        for (Region region : commandForRegion) {
            instance.getConfig().set("regions." + index + ".regionName", region.getRegionName());
            instance.getConfig().set("regions." + index + ".eventType", region.getEventType().getEventName());
            instance.getConfig().set("regions." + index + ".command", region.getCommand());
            index++;
        }

        instance.saveConfig();
    }
}
