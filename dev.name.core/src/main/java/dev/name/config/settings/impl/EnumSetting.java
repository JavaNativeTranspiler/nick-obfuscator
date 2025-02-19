package dev.name.config.settings.impl;

import dev.name.config.settings.Setting;

public class EnumSetting<T extends Enum<T>> extends Setting {
    private final Class<T> klass;

    @SuppressWarnings("unchecked")
    public EnumSetting(final String name, final Enum<?> dflt) {
        super(name, dflt);
        this.klass = (Class<T>) dflt.getDeclaringClass();
    }

    @Override
    public Enum<?> getValue() {
        return (Enum<?>) super.getValue();
    }

    @Override
    public void setValue(final Object value) {
        if (value instanceof String str) super.setValue(Enum.valueOf(klass, str.toUpperCase()));
        else if (value instanceof Enum) super.setValue(value);
    }
}