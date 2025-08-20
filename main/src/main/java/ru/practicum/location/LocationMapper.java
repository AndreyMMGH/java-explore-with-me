package ru.practicum.location;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.lat,
                location.lon
        );
    }

    public static Location toLocation(LocationDto locationDto) {
        return new Location(
                null,
                locationDto.getLat(),
                locationDto.getLon()
        );
    }
}
