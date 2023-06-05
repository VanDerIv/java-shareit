package ru.practicum.shareit;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GlobalProperties {
    private GlobalProperties() {
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT_SSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final ZoneId DATE_ZONE_ID = ZoneId.of("Europe/Moscow");
}
