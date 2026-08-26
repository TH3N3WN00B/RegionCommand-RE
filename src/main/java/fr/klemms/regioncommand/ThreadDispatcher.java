package fr.klemms.regioncommand;

import org.bukkit.Bukkit;

public class ThreadDispatcher {

    public static void runAsync(RegionCommand plugin, Runnable task) {
        if (Config.useVirtualThreads) {
            Thread.startVirtualThread(task);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
}
