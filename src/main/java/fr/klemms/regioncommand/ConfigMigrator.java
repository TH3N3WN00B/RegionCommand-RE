package fr.klemms.regioncommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class ConfigMigrator {

    private static final int OLD_PLUGIN_VERSION = 1;
    private static final int NEW_PLUGIN_VERSION = 2;

    public static boolean migrate(RegionCommand plugin) {
        File dataFolder = plugin.getDataFolder();
        File configFile = new File(dataFolder, "config.yml");

        if (!configFile.exists()) {
            plugin.getLogger().info("Fresh install detected. No migration needed.");
            setPluginVersion(plugin, NEW_PLUGIN_VERSION);
            return false;
        }

        int currentVersion = plugin.getConfig().getInt("pluginVersion", 0);

        if (currentVersion >= NEW_PLUGIN_VERSION) {
            plugin.getLogger().info("Config is already up to date (version " + currentVersion + ").");
            return false;
        }

        plugin.getLogger().info("Migrating config from version " + currentVersion + " to " + NEW_PLUGIN_VERSION + "...");

        backupConfig(plugin, dataFolder);
        cleanupOldFiles(dataFolder);

        setPluginVersion(plugin, NEW_PLUGIN_VERSION);
        plugin.saveConfig();

        plugin.getLogger().info("Config migration completed successfully!");
        return true;
    }

    private static void backupConfig(RegionCommand plugin, File dataFolder) {
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) return;

        File backupFolder = new File(dataFolder, "backups");
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        File backupFile = new File(backupFolder, "config_v1_" + timestamp + ".yml");

        try {
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().info("Old config backed up to: backups/" + backupFile.getName());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to backup old config", e);
        }
    }

    private static void cleanupOldFiles(File dataFolder) {
        Path klemmsPlugins = dataFolder.toPath().getParent().resolve("KlemmsPlugins");
        if (Files.exists(klemmsPlugins)) {
            File updateConfig = klemmsPlugins.resolve("update_config.yml").toFile();
            if (updateConfig.exists()) {
                updateConfig.delete();
            }
            try {
                if (Files.list(klemmsPlugins).findAny().isEmpty()) {
                    Files.deleteIfExists(klemmsPlugins);
                }
            } catch (IOException ignored) {
            }
        }
    }

    private static void setPluginVersion(RegionCommand plugin, int version) {
        plugin.getConfig().set("pluginVersion", version);
    }
}
