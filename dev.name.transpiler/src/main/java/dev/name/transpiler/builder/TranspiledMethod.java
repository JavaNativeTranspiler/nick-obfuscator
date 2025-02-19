package dev.name.transpiler.builder;

import dev.name.asm.ir.components.Method;
import dev.name.transpiler.types.LabelMap;
import dev.name.transpiler.types.MethodDefinition;
import dev.name.transpiler.types.VarPool;

public class TranspiledMethod {
    public final LabelMap labelMap;
    public final VarPool varPool;
    public final MethodDefinition definition;
    public final TranspiledClass klass;
    public final int maxStack;
    public final StringBuilder output = new StringBuilder();
    //public final Cache cache;

    private TranspiledMethod(final TranspiledClass parent, final Method method) {
        this.labelMap = LabelMap.create(method);
        this.varPool = VarPool.create(method);
        this.definition = MethodDefinition.create(method);
        varPool.merge(definition.parameters);
        this.klass = parent;
        this.maxStack = method.maxStack;
        //this.cache = Cache.from(definition.FUNCTION_NAME);
    }

    public static TranspiledMethod create(final TranspiledClass parent, final Method method) {
        return new TranspiledMethod(parent, method);
    }
}