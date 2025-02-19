package dev.name.transformer;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Field;
import dev.name.asm.ir.components.Method;
import dev.name.config.settings.Setting;
import dev.name.config.settings.impl.*;
import dev.name.util.java.ClassPool;
import dev.name.util.java.Jar;
import lombok.Setter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Transformer implements Opcodes {
    @Setter public Jar jar;

    public abstract String name();
    public abstract void transform(final ClassPool pool);

    public final ArrayList<Setting> settings = new ArrayList<>();
    public final BooleanSetting enabled = setting("Enabled", true);

    public final BooleanSetting setting(final String name, boolean dflt){
        final BooleanSetting setting = new BooleanSetting(name, dflt);
        settings.add(setting);
        return setting;
    }

    public final EnumSetting<?> setting(final String name, final Enum<?> dflt){
        final EnumSetting<?> setting = new EnumSetting<>(name, dflt);
        settings.add(setting);
        return setting;
    }

    public final FloatSetting setting(final String name, float dflt){
        final FloatSetting setting = new FloatSetting(name, dflt);
        settings.add(setting);
        return setting;
    }

    public final IntSetting setting(final String name, final int dflt){
        IntSetting setting = new IntSetting(name, dflt);
        settings.add(setting);
        return setting;
    }

    public final StringSetting setting(final String name, final String dflt){
        final StringSetting setting = new StringSetting(name, dflt);
        settings.add(setting);
        return setting;
    }

    public final StringArraySetting setting(final String name, final String[] dflt){
        final StringArraySetting setting = new StringArraySetting(name, dflt);
        settings.add(setting);
        return setting;
    }

    public final boolean isEnabled(){
        return enabled.getValue();
    }

    public final boolean isExcluded(Class classNode) {
        return getExclusions() != null && Arrays.stream(getExclusions().getValue()).anyMatch(exclusion -> classNode.name.startsWith(exclusion));
    }

    public final boolean isExcluded(Class classNode, Method methodNode) {
        final String method = classNode.name + "." + methodNode.name;
        return getExclusions() != null && Arrays.stream(getExclusions().getValue()).anyMatch(exclusion -> method.startsWith(exclusion) || (method + methodNode.desc).startsWith(exclusion));
    }

    public final boolean isExcluded(Class classNode, Field fieldNode) {
        final String field = classNode.name + "." + fieldNode.name;
        return getExclusions() != null && Arrays.stream(getExclusions().getValue()).anyMatch(exclusion -> field.startsWith(exclusion) || (field + fieldNode.desc).startsWith(exclusion));
    }

    public StringArraySetting getExclusions() {
        return null;
    }
}