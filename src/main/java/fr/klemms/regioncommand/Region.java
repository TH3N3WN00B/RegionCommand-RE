package fr.klemms.regioncommand;

public class Region {

    private String regionName;
    private EventType eventType;
    private String command;
    private final int id;

    public Region(String regionName, EventType eventType, String command, int id) {
        this.regionName = regionName;
        this.eventType = eventType;
        this.command = command;
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getId() {
        return id;
    }
}
