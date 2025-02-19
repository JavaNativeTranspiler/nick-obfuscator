package dev.name.config.settings.impl;

import dev.name.config.settings.Setting;

public class IntSetting extends Setting {
    public IntSetting(final String name, final int dflt) {
        super(name, dflt);
    }

    @Override
    public Integer getValue() {
        if (super.getValue() instanceof Integer) return (Integer) super.getValue();
        return ((Double) super.getValue()).intValue();
    }
}