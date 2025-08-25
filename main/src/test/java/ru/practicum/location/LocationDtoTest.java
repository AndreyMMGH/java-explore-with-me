package ru.practicum.location;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.location.dto.LocationDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class LocationDtoTest {

    @Autowired
    private JacksonTester<LocationDto> jsonLocationDto;

    @Test
    void shouldSerializeLocationDto() throws Exception {
        LocationDto dto = new LocationDto(50.01f, 31.25f);

        JsonContent<LocationDto> result = jsonLocationDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.lat").isEqualTo(50.01);
        assertThat(result).extractingJsonPathNumberValue("$.lon").isEqualTo(31.25);
    }

    @Test
    void shouldDeserializeLocationDto() throws Exception {
        String json = "{\"lat\": 50.01, \"lon\": 31.25}";

        LocationDto dto = jsonLocationDto.parseObject(json);

        assertThat(dto.getLat()).isEqualTo(50.01f);
        assertThat(dto.getLon()).isEqualTo(31.25f);
    }
}
