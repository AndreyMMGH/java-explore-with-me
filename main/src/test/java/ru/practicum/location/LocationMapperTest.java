package ru.practicum.location;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.mapper.LocationMapper;
import ru.practicum.location.model.Location;

class LocationMapperTest {

    @Test
    void shouldLocationToLocationDto() {
        Location location = new Location(1L, 50.01f, 31.25f);

        LocationDto result = LocationMapper.toLocationDto(location);

        AssertionsForClassTypes.assertThat(result.getLat()).isEqualTo(50.01f);
        AssertionsForClassTypes.assertThat(result.getLon()).isEqualTo(31.25f);
    }

    @Test
    void shouldLocationDtoToLocation() {
        LocationDto dto = new LocationDto(50.01f, 31.25f);

        Location result = LocationMapper.toLocation(dto);

        AssertionsForClassTypes.assertThat(result.getId()).isNull();
        AssertionsForClassTypes.assertThat(result.getLat()).isEqualTo(50.01f);
        AssertionsForClassTypes.assertThat(result.getLon()).isEqualTo(31.25f);
    }
}
