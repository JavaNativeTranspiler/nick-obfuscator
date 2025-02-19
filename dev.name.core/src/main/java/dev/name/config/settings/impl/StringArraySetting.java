package dev.name.config.settings.impl;

import dev.name.config.settings.Setting;

public class StringArraySetting extends Setting {
    public StringArraySetting(final String name, String[] dflt) {
        super(name, dflt);
    }

    @Override
    public String[] getValue() {
        return (String[]) super.getValue();
    }
}
