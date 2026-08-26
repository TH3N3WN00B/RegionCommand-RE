package fr.klemms.regioncommand;

public enum EventType {
    ENTER("enter"),
    LEAVE("leave");

    private final String eventName;

    EventType(String eventName) {
        this.eventName = eventName;
    }

    public static EventType getEventTypeByName(String eventType) {
        if (eventType.equalsIgnoreCase("enter")) {
            return ENTER;
        }
        return LEAVE;
    }

    public String getEventName() {
        return eventName;
    }
}
