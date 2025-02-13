package ru.eventlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
    @NotBlank
    String app;
    @NotBlank
    String uri;
    @NotNull
    Long hits;
}
