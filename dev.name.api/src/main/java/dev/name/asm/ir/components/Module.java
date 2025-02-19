package dev.name.asm.ir.components;

import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.types.Access;
import dev.name.asm.ir.types.Flags;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public final class Module extends ModuleVisitor implements Opcodes {
    public Access access;
    public String name, version, main;
    public List<String> packages = new ArrayList<>(), uses = new ArrayList<>();
    public List<Require> requires = new ArrayList<>();
    public List<Export> exports = new ArrayList<>();
    public List<Open> opens = new ArrayList<>();
    public List<Provide> provides = new ArrayList<>();
    //
    public Flags flags = new Flags();

    public Module(final String name, final Access access, final String version) {
        super(ASM9);
        this.name = name;
        this.access = access;
        this.version = version;
    }

    public Module(final String name, final int access, final String version) {
        this(name, new Access(access), version);
    }

    public Module(final String name, final Access access, final String version, final List<Require> requires, final List<Export> exports, final List<Open> opens, final List<String> uses, final List<Provide> provides) {
        super(ASM9);
        this.name = name;
        this.access = access;
        this.version = version;
        this.requires = requires;
        this.exports = exports;
        this.opens = opens;
        this.uses = uses;
        this.provides = provides;
    }

    public Module(final String name, final int access, final String version, final List<Require> requires, final List<Export> exports, final List<Open> opens, final List<String> uses, final List<Provide> provides) {
        this(name, new Access(access), version, requires, exports, opens, uses, provides);
    }

    @Override
    public void visitMainClass(final String main) {
        this.main = main;
    }

    @Override
    public void visitPackage(final String pkg) {
        packages.add(pkg);
    }

    @Override
    public void visitRequire(final String module, final int access, final String version) {
        requires.add(new Require(module, access, version));
    }

    @Override
    public void visitExport(final String pkg, final int access, final String... modules) {
        exports.add(new Export(pkg, access, modules != null ? new ArrayList<>(List.of(modules)) : List.of()));
    }

    @Override
    public void visitOpen(final String pkg, final int access, final String... modules) {
        opens.add(new Open(pkg, access, new ArrayList<>(List.of(modules))));
    }

    @Override
    public void visitUse(final String service) {
        uses.add(service);
    }

    @Override
    public void visitProvide(final String service, final String... providers) {
        provides.add(new Provide(service, new ArrayList<>(List.of(providers))));
    }

    @Override
    public void visitEnd() {
        if (Global.PREPROCESSING) Processor.process(this, Processor.Mode.PRE);
    }

    public void accept(final ClassVisitor visitor) {
        if (Global.POSTPROCESSING) Processor.process(this, Processor.Mode.POST);
        if (visitor == null || access == null) throw new IllegalStateException();
        ModuleVisitor module = visitor.visitModule(name, access.getAccess(), version);
        if (module == null) throw new IllegalStateException();
        if (main != null) module.visitMainClass(main);
        for (final String pkg : packages) module.visitPackage(pkg);
        for (final Require require : requires) require.accept(module);
        for (final Export export : exports) export.accept(module);
        for (final Open open : opens) open.accept(module);
        for (final String usage : uses) module.visitUse(usage);
        for (final Provide provide : provides) provide.accept(module);
    }

    public static final class Export {
        public String pkg;
        public Access access;

        public List<String> modules;

        public Export(final String pkg, final Access access, final List<String> modules) {
            this.pkg = pkg;
            this.access = access;
            this.modules = modules;
        }

        public Export(final String pkg, final int access, final List<String> modules) {
            this(pkg, new Access(access), modules);
        }

        public void accept(final ModuleVisitor visitor) {
            if (access == null) throw new IllegalStateException();
            visitor.visitExport(pkg, access.getAccess(), modules == null ? null : modules.toArray(new String[0]));
        }
    }

    public static final class Open {
        public String pkg;
        public Access access;
        public List<String> modules;

        public Open(final String pkg, final Access access, final List<String> modules) {
            this.pkg = pkg;
            this.access = access;
            this.modules = modules;
        }

        public Open(final String pkg, final int access, final List<String> modules) {
            this(pkg, new Access(access), modules);
        }

        public void accept(final ModuleVisitor visitor) {
            if (access == null) throw new IllegalStateException();
            visitor.visitOpen(pkg, access.getAccess(), modules == null ? null : modules.toArray(new String[0]));
        }
    }


    public static final class Provide {
        public String service;
        public List<String> providers;

        public Provide(final String service, final List<String> providers) {
            this.service = service;
            this.providers = providers;
        }

        public void accept(final ModuleVisitor visitor) {
            visitor.visitProvide(service, providers.toArray(new String[0]));
        }
    }

    public static final class Require {
        public String module;
        public Access access;
        public String version;

        public Require(final String module, final Access access, final String version) {
            this.module = module;
            this.access = access;
            this.version = version;
        }

        public Require(final String module, final int access, final String version) {
            this(module, new Access(access), version);
        }

        public void accept(final ModuleVisitor visitor) {
            if (access == null) throw new IllegalStateException();
            visitor.visitRequire(module, access.getAccess(), version);
        }
    }
}