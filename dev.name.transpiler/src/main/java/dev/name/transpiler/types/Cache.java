package dev.name.transpiler.types;

import dev.name.asm.ir.nodes.Accessor;
import dev.name.asm.ir.nodes.Instruction;
import dev.name.asm.ir.nodes.Invoke;
import dev.name.asm.ir.types.Node;
import dev.name.transpiler.encryption.StringProcessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Cache implements Opcodes {
    private static final String FORMAT = """
            struct %s final
            {
            %s
            };
            
            static __forceinline %s* %s(JNIEnv* env) noexcept
            {
                static struct %s cache;
                if (!cache.klass)
                {
            %s
                }
                return &cache;
            }
            """;

    @Getter private static final Map<String, Cache> caches = new HashMap<>();

    public static Cache from(final String name) {
        return caches.computeIfAbsent(name, c -> new Cache(name));
    }

    @AllArgsConstructor
    @SuppressWarnings("all")
    private static class Shard {
        public final String identifier, type, form;
        public final Node node;

        @Override
        public String toString() {
            return String.format("%s = %s", identifier, form);
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Shard shard)) return false;
            return shard.form.equals(this.form);
        }
    }

    @Getter private final List<Shard> shards = new LinkedList<>();
    private final String struct, resolver;
    private int id = 0;

    private Cache(final String node) {
        shards.add(new Shard("klass", "jclass", String.format("(jclass) env->NewGlobalRef(env->FindClass(%s));", StringProcessor.resolve(node)), new Instruction(-1)));
        this.struct = node.replaceAll("[/.$]", "_").replace("[", "ARRAY_").replace(";", "");
        this.resolver = this.struct.concat("_create");
    }

    public void add(final Invoke method) {
        final int opcode = method.opcode;
        final Shard shard = new Shard(reserve_next(opcode), "jmethodID", form(opcode, method), method);
        if (shards.contains(shard)) {
            id--;
            return;
        }
        shards.add(shard);
    }

    public void add(final Accessor field) {
        final int opcode = field.opcode;
        final Shard shard = new Shard(reserve_next(opcode), "jfieldID", form(opcode, field), field);
        if (shards.contains(shard)) {
            id--;
            return;
        }
        shards.add(shard);
    }

    private String form(final int opcode, final Accessor field) {
        final String _access = (opcode == GETSTATIC || opcode == PUTSTATIC) ? "Static" : "";
        return String.format("env->Get%sFieldID(cache.klass, %s, %s);", _access, StringProcessor.resolve(field.name), StringProcessor.resolve(field.desc));
    }

    private String form(final int opcode, final Invoke method) {
        final String _access = opcode == INVOKESTATIC ? "Static" : "";
        return String.format("env->Get%sMethodID(cache.klass, %s, %s);", _access, StringProcessor.resolve(method.name), StringProcessor.resolve(method.desc));
    }

    private String reserve_next(final int opcode) {
        return String.format("%s_%s", trim(opcode), id++);
    }

    private static String trim(final int opcode) {
        return switch (opcode) {
            case GETFIELD -> "getfield";
            case PUTFIELD -> "putfield";
            case GETSTATIC -> "getstatic";
            case PUTSTATIC -> "putstatic";
            case INVOKESTATIC -> "invokestatic";
            case INVOKEVIRTUAL -> "invokevirtual";
            case INVOKEINTERFACE -> "invokeinterface";
            case INVOKEDYNAMIC -> "invokedynamic";
            case INVOKESPECIAL -> "invokespecial";
            default -> throw new RuntimeException("not good.");
        };
    }

    public String struct() {
        return struct;
    }

    public String resolver() {
        return resolver;
    }

    public String getIdentifier(final Accessor field) {
        return shards.stream()
                .filter(shard -> shard.node.equals(field))
                .findFirst()
                .map(shard -> shard.identifier)
                .orElseGet(() -> {
                    final Shard shard = new Shard(reserve_next(field.opcode), "jfieldID", form(field.opcode, field), field);
                    shards.add(shard);
                    return shard.identifier;
                });
    }

    public String getIdentifier(final Invoke method) {
        return shards.stream()
                .filter(shard -> shard.node.equals(method))
                .findFirst()
                .map(shard -> shard.identifier)
                .orElseGet(() -> {
                    final Shard shard = new Shard(reserve_next(method.opcode), "jmethodID", form(method.opcode, method), method);
                    shards.add(shard);
                    return shard.identifier;
                });
    }

    public String format() {
        final StringBuilder struct_declaration = new StringBuilder();
        final StringBuilder struct_creation = new StringBuilder();
        final Shard last = shards.get(shards.size() - 1);

        for (final Shard shard : shards) {
            struct_declaration.append("    ").append(shard.type).append(' ').append(shard.identifier).append(';');
            struct_creation.append("        cache.").append(shard);
            if (!shard.equals(last)) {
                struct_declaration.append('\n');
                struct_creation.append('\n');
            }
        }

        return String.format(
                FORMAT,
                this.struct,
                struct_declaration,
                this.struct,
                this.resolver,
                this.struct,
                struct_creation
        );
    }

    @Override
    public String toString() {
        return format();
    }
}