package fr.klemms.regioncommand;

import org.bukkit.Bukkit;

public class ThreadDispatcher {

    private static final boolean VIRTUAL_THREADS_SUPPORTED = isVirtualThreadsSupported();

    public static void runAsync(RegionCommand plugin, Runnable task) {
        if (VIRTUAL_THREADS_SUPPORTED) {
            Thread.startVirtualThread(task);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    private static boolean isVirtualThreadsSupported() {
        try {
            Thread.class.getMethod("startVirtualThread", Runnable.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
