package dev.name.config.settings.impl;

import dev.name.config.settings.Setting;

public class StringSetting extends Setting {
    public StringSetting(final String name, String dflt) {
        super(name, dflt);
    }

    @Override
    public String getValue() {
        return (String) super.getValue();
    }
}
