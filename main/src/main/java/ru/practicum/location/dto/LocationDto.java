package ru.practicum.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private Float lat;

    @Min(-180)
    @Max(180)
    @NotNull
    private Float lon;
}
