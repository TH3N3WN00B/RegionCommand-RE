package fr.klemms.regioncommand;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static void readConfig(RegionCommand plugin) {
        FileConfiguration config = plugin.getConfig();
        int regionsN = config.getInt("regionsN", 0);

        for (int a = 0; a < regionsN; a++) {
            String regionName = config.getString("regions." + a + ".regionName");
            String eventTypeName = config.getString("regions." + a + ".eventType");
            String command = config.getString("regions." + a + ".command");

            if (regionName == null || eventTypeName == null || command == null) continue;

            EventType eventType = EventType.getEventTypeByName(eventTypeName);
            Region region = new Region(regionName, eventType, command, RegionCommand.nextCommandID++);
            RegionCommand.commandForRegion.add(region);
        }

        RegionCommand.saveToDisk();
    }
}
