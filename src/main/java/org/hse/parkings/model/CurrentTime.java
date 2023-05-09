package org.hse.parkings.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
public class CurrentTime {

    @JsonFormat(with = JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID)
    ZonedDateTime time;
}
