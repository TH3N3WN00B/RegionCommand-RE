package fr.klemms.regioncommand;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class UpdateChecker implements Listener {

    private static final String GITHUB_API = "https://api.github.com/repos/TH3N3WN00B/RegionCommand-RE/releases/latest";
    private static final String DOWNLOAD_URL_PREFIX = "https://github.com/TH3N3WN00B/RegionCommand-RE/releases/latest/download/";

    private final RegionCommand plugin;
    private String latestVersion;
    private String downloadUrl;
    private boolean updateAvailable = false;

    public UpdateChecker(RegionCommand plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GITHUB_API))
                        .header("Accept", "application/vnd.github.v3+json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    plugin.getLogger().warning("Update check failed: HTTP " + response.statusCode());
                    return;
                }

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                latestVersion = json.get("tag_name").getAsString().replace("v", "");

                String currentVersion = plugin.getDescription().getVersion();
                if (isNewerVersion(latestVersion, currentVersion)) {
                    updateAvailable = true;
                    plugin.getLogger().info("Update available: " + latestVersion + " (current: " + currentVersion + ")");

                    JsonObject asset = json.getAsJsonArray("assets").get(0).getAsJsonObject();
                    downloadUrl = asset.get("browser_download_url").getAsString();

                    Bukkit.getScheduler().runTask(plugin, () -> notifyOps());
                } else {
                    plugin.getLogger().info("Plugin is up to date (v" + currentVersion + ")");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to check for updates", e);
            }
        });
    }

    private void notifyOps() {
        if (!updateAvailable) return;

        Component message = Component.text("[RegionCommand] Update available: v" + latestVersion + " ", NamedTextColor.GOLD)
                .append(Component.text("[Click to update]", NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/regioncommandupdate")));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("regioncommand.update")) {
                player.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (updateAvailable) {
            Player player = event.getPlayer();
            if (player.hasPermission("regioncommand.update")) {
                Component message = Component.text("[RegionCommand] Update available: v" + latestVersion + " ", NamedTextColor.GOLD)
                        .append(Component.text("[Click to update]", NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/regioncommandupdate")));
                player.sendMessage(message);
            }
        }
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public static boolean isNewerVersion(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");

        int maxLength = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < maxLength; i++) {
            int latestNum = i < latestParts.length ? parsePart(latestParts[i]) : 0;
            int currentNum = i < currentParts.length ? parsePart(currentParts[i]) : 0;
            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false;
    }

    private static int parsePart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
