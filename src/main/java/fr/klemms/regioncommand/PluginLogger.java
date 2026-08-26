package fr.klemms.regioncommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PluginLogger {

    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yy-HH-mm");
    private static final DateTimeFormatter HEADER_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static File getLogsFolder(RegionCommand plugin) {
        File logsFolder = new File(plugin.getDataFolder(), "logs");
        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
        return logsFolder;
    }

    public static void logCrash(RegionCommand plugin, Throwable throwable) {
        writeLog(plugin, "crash-log", throwable, "CRASH REPORT - Plugin failed to start");
    }

    public static void logError(RegionCommand plugin, String context, Throwable throwable) {
        writeLog(plugin, "error-log", throwable, "ERROR REPORT - " + context);
    }

    public static void logError(RegionCommand plugin, String message) {
        writeLog(plugin, "error-log", message, "ERROR REPORT");
    }

    private static void writeLog(RegionCommand plugin, String prefix, Throwable throwable, String title) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        writeLog(plugin, prefix, title, title + "\n\nException:\n" + sw.toString());
    }

    private static void writeLog(RegionCommand plugin, String prefix, String title, String content) {
        String timestamp = LocalDateTime.now().format(FILE_FORMAT);
        String fileName = prefix + "-" + timestamp + ".log";
        File logFile = new File(getLogsFolder(plugin), fileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile))) {
            writer.println("==========================================");
            writer.println("  RegionCommand " + title);
            writer.println("==========================================");
            writer.println("Timestamp: " + LocalDateTime.now().format(HEADER_FORMAT));
            writer.println("Plugin Version: " + plugin.getDescription().getVersion());
            writer.println("Server Version: " + plugin.getServer().getVersion());
            writer.println("Java Version: " + System.getProperty("java.version"));
            writer.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            writer.println("==========================================");
            writer.println();
            writer.println(content);
            writer.println();
            writer.println("==========================================");
            writer.println("  End of Report");
            writer.println("==========================================");

            plugin.getLogger().warning("Log saved to: logs/" + fileName);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write log file: " + e.getMessage());
        }
    }
}
