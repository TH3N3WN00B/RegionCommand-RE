package fr.klemms.regioncommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class AutoUpdater implements CommandExecutor {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

    private final RegionCommand plugin;
    private final UpdateChecker updateChecker;
    private final AtomicBoolean updating = new AtomicBoolean(false);

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

        if (!updating.compareAndSet(false, true)) {
            sender.sendMessage(Component.text("Update already in progress...", NamedTextColor.YELLOW));
            return true;
        }

        if (!updateChecker.isUpdateAvailable()) {
            updating.set(false);
            sender.sendMessage(Component.text("Plugin is already up to date!", NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.text("Downloading update v" + updateChecker.getLatestVersion() + "...", NamedTextColor.YELLOW));

        ThreadDispatcher.runAsync(plugin, () -> {
            File pluginFile = locatePluginFile();
            try {
                if (pluginFile == null) {
                    sendToMain(sender, Component.text("Could not locate plugin file!", NamedTextColor.RED));
                    return;
                }

                File updateFile = new File(pluginFile.getParent(), pluginFile.getName() + ".tmp");
                downloadFile(updateChecker.getDownloadUrl(), updateFile);

                File backupFile = new File(pluginFile.getParent(), pluginFile.getName() + ".bak");
                Files.copy(pluginFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Files.move(updateFile.toPath(), pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                sendToMain(sender, Component.text("Update downloaded successfully!", NamedTextColor.GREEN));
                sendToMain(sender, Component.text("Restart the server when ready to apply the update.", NamedTextColor.YELLOW));
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to update plugin", e);
                sendToMain(sender, Component.text("Update failed: " + e.getMessage(), NamedTextColor.RED));
                cleanupTempFiles(pluginFile);
            } finally {
                updating.set(false);
            }
        });

        return true;
    }

    private void sendToMain(CommandSender sender, Component message) {
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage(message));
    }

    private File locatePluginFile() {
        File dataFolder = plugin.getDataFolder();
        File[] jars = dataFolder.getParentFile().listFiles((dir, name) ->
                name.toLowerCase().startsWith("regioncommand") && name.toLowerCase().endsWith(".jar"));
        if (jars != null && jars.length > 0) {
            return jars[0];
        }
        return null;
    }

    private void downloadFile(String url, File target) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new IOException("Download failed: HTTP " + response.statusCode());
        }

        try (InputStream in = response.body()) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void cleanupTempFiles(File pluginFile) {
        if (pluginFile == null) return;
        File tmp = new File(pluginFile.getParent(), pluginFile.getName() + ".tmp");
        if (tmp.exists()) tmp.delete();
    }
}
