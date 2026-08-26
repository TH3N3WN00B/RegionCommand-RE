package fr.klemms.regioncommand;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.logging.Level;

public class ConfigUpdater {

    public static void update(RegionCommand plugin) {
        File dataFolder = plugin.getDataFolder();
        File configFile = new File(dataFolder, "config.yml");

        FileConfiguration userConfig = plugin.getConfig();
        FileConfiguration defaultConfig;

        try (InputStream in = plugin.getResource("config.yml")) {
            if (in == null) {
                plugin.getLogger().warning("No default config.yml found in jar.");
                return;
            }
            defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load default config from jar", e);
            return;
        }

        int added = mergeSection(userConfig, defaultConfig, "");

        if (added > 0) {
            plugin.saveConfig();
            plugin.getLogger().info("Config updated: " + added + " new option(s) added. Your existing settings were preserved.");
        }
    }

    private static int mergeSection(FileConfiguration userConfig, FileConfiguration defaultConfig, String path) {
        int added = 0;

        Set<String> keys = defaultConfig.getKeys(false);
        for (String key : keys) {
            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (defaultConfig.isConfigurationSection(key)) {
                if (!userConfig.contains(fullPath)) {
                    userConfig.createSection(fullPath);
                }
                added += mergeSection(userConfig, defaultConfig, fullPath);
            } else {
                if (!userConfig.contains(fullPath)) {
                    userConfig.set(fullPath, defaultConfig.get(key));
                    added++;
                    plugin().getLogger().info("Added new config option: " + fullPath);
                }
            }
        }

        return added;
    }

    private static RegionCommand plugin() {
        return RegionCommand.instance;
    }
}
