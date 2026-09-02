package resourcemanager.model.tables;

import java.time.LocalTime;
import java.util.Map;

public class ResouceInfoRow {
    private final LocalTime hour;
    private final Map<String, ResourceInfoCell> byResourceId; // se usa map porque cada elemento esta asociado a un id y no puede ser duplicado

    public  ResouceInfoRow(LocalTime hour, Map<String, ResourceInfoCell> byResourceId) {
        this.hour = hour;
        this.byResourceId = byResourceId;
    }

    public LocalTime getHour() { return hour; }
    public ResourceInfoCell getInfo(String resourceId) { return byResourceId.get(resourceId); }
}
