package dev.name.config.settings.impl;

import dev.name.config.settings.Setting;

public class BooleanSetting extends Setting {
    public BooleanSetting(final String name, boolean dflt) {
        super(name, dflt);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) super.getValue();
    }
}
