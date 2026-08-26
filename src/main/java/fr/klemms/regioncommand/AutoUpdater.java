package fr.klemms.regioncommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class AutoUpdater implements CommandExecutor {

    private final RegionCommand plugin;
    private final UpdateChecker updateChecker;
    private boolean updating = false;

    public AutoUpdater(RegionCommand plugin, UpdateChecker updateChecker) {
        this.plugin = plugin;
        this.updateChecker = updateChecker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("regioncommand.update")) {
            sender.sendMessage(Component.text("You don't have permission to update.", NamedTextColor.RED));
            return true;
        }

        if (updating) {
            sender.sendMessage(Component.text("Update already in progress...", NamedTextColor.YELLOW));
            return true;
        }

        if (!updateChecker.isUpdateAvailable()) {
            sender.sendMessage(Component.text("Plugin is already up to date!", NamedTextColor.GREEN));
            return true;
        }

        updating = true;
        sender.sendMessage(Component.text("Downloading update v" + updateChecker.getLatestVersion() + "...", NamedTextColor.YELLOW));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                File pluginFile = getPluginFile();
                if (pluginFile == null) {
                    sender.sendMessage(Component.text("Could not locate plugin file!", NamedTextColor.RED));
                    updating = false;
                    return;
                }

                File updateFile = new File(pluginFile.getParent(), "RegionCommand.jar.tmp");

                downloadFile(updateChecker.getDownloadUrl(), updateFile);

                File backupFile = new File(pluginFile.getParent(), "RegionCommand.jar.bak");
                Files.copy(pluginFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Files.move(updateFile.toPath(), pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(Component.text("Update downloaded successfully!", NamedTextColor.GREEN));
                    sender.sendMessage(Component.text("Restart the server when ready to apply the update.", NamedTextColor.YELLOW));
                    updating = false;
                });
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to update plugin", e);
                sender.sendMessage(Component.text("Update failed: " + e.getMessage(), NamedTextColor.RED));
                cleanupTempFiles();
                updating = false;
            }
        });

        return true;
    }

    private void downloadFile(String url, File target) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new IOException("Download failed: HTTP " + response.statusCode());
        }

        try (InputStream in = response.body()) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private File getPluginFile() {
        File dataFolder = plugin.getDataFolder();
        File pluginsFolder = dataFolder.getParentFile();

        File[] jars = pluginsFolder.listFiles((dir, name) ->
                name.toLowerCase().startsWith("regioncommand") && name.toLowerCase().endsWith(".jar")
        );

        if (jars != null && jars.length > 0) {
            return jars[0];
        }
        return null;
    }

    private void cleanupTempFiles() {
        File dataFolder = plugin.getDataFolder();
        File pluginsFolder = dataFolder.getParentFile();

        File tmp = new File(pluginsFolder, "RegionCommand.jar.tmp");
        if (tmp.exists()) tmp.delete();
    }
}
