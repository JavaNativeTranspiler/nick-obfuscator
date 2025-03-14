package dev.name.transformer;

import dev.name.transformer.transformers.deobf.generic.Folder;
import dev.name.transformer.transformers.obf.flow.LoopMutationTransformer;
import dev.name.transformer.transformers.obf.flow.table.DispatchTableTransformer;
import dev.name.transformer.transformers.obf.hash.HashTransformer;
import dev.name.transformer.transformers.obf.string.StringEncryptionTransformer;
import dev.name.util.java.ClassPool;
import dev.name.util.java.Jar;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

public class Transformers {
    @Getter
    private static final Set<Transformer> transformers = new LinkedHashSet<>()
    {
        {
            add(new Folder());
        }
    };

    public static void call(final Jar jar) {
        final ClassPool pool = jar.getClasses();
        for (Transformer transformer : transformers) {
            //if (!transformer.isEnabled()) continue;
            transformer.setJar(jar);
            transformer.transform(pool);
            System.out.println(transformer.name());
        }
    }
}