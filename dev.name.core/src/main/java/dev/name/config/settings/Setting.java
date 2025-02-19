package dev.name.config.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Setting {
    private final String name;
    @Setter private Object value;
}