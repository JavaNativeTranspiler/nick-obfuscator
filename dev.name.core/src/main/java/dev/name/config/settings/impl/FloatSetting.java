package dev.name.config.settings.impl;

import dev.name.config.settings.Setting;

public class FloatSetting extends Setting {

    public FloatSetting(final String name, float dflt) {
        super(name, dflt);
    }

    @Override
    public Float getValue() {
        if (super.getValue() instanceof Float) return (Float) super.getValue();
        return ((Double) super.getValue()).floatValue();
    }
}