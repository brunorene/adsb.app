package pt.brene.adsb;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter extends StdConverter<LocalDateTime, String> {
    @Override
    public String convert(LocalDateTime value) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value);
    }
}
