package dev.name.asm.ir.types;

import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.Opcodes;

@Setter
@Getter
@SuppressWarnings("unused")
public class Access implements Opcodes {
    public static class Builder {
        private final Access access = new Access(0);

        public Access.Builder _public() {
            this.access.setPublic(true);
            return this;
        }

        public Access.Builder _private() {
            this.access.setPrivate(true);
            return this;
        }

        public Access.Builder _protected() {
            this.access.setProtected(true);
            return this;
        }

        public Access.Builder _static() {
            this.access.setStatic(true);
            return this;
        }

        public Access.Builder _final() {
            this.access.setFinal(true);
            return this;
        }

        public Access.Builder _synchronized() {
            this.access.setSynchronized(true);
            return this;
        }

        public Access.Builder _volatile() {
            this.access.setVolatile(true);
            return this;
        }

        public Access.Builder _transient() {
            this.access.setTransient(true);
            return this;
        }

        public Access.Builder _native() {
            this.access.setNative(true);
            return this;
        }

        public Access.Builder _interface() {
            this.access.setInterface(true);
            return this;
        }

        public Access.Builder _abstract() {
            this.access.setAbstract(true);
            return this;
        }

        public Access.Builder _strictfp() {
            this.access.setStrictfp(true);
            return this;
        }

        public Access.Builder _synthetic() {
            this.access.setSynthetic(true);
            return this;
        }

        public Access.Builder _annotation() {
            this.access.setAnnotation(true);
            return this;
        }

        public Access.Builder _enum() {
            this.access.setEnum(true);
            return this;
        }

        public Access.Builder _super() {
            this.access.setSuper(true);
            return this;
        }

        public Access.Builder _bridge() {
            this.access.setBridge(true);
            return this;
        }

        public Access.Builder _varargs() {
            this.access.setVarargs(true);
            return this;
        }

        public Access build() {
            return this.access;
        }
    }

    private int access;

    public Access(final int access) {
        this.access = access;
    }

    public static Access.Builder builder() {
        return new Builder();
    }

    public static Access create() {
        return new Access(0);
    }

    public static Access create(final int access) {
        return new Access(access);
    }

    public Access copy() {
        return new Access(this.access);
    }

    public boolean isPublic() {
        return (access & ACC_PUBLIC) != 0;
    }

    public boolean isPrivate() {
        return (access & ACC_PRIVATE) != 0;
    }

    public boolean isProtected() {
        return (access & ACC_PROTECTED) != 0;
    }

    public boolean isStatic() {
        return (access & ACC_STATIC) != 0;
    }

    public boolean isFinal() {
        return (access & ACC_FINAL) != 0;
    }

    public boolean isSynchronized() {
        return (access & ACC_SYNCHRONIZED) != 0;
    }

    public boolean isVolatile() {
        return (access & ACC_VOLATILE) != 0;
    }

    public boolean isTransient() {
        return (access & ACC_TRANSIENT) != 0;
    }

    public boolean isNative() {
        return (access & ACC_NATIVE) != 0;
    }

    public boolean isInterface() {
        return (access & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (access & ACC_ABSTRACT) != 0;
    }

    public boolean isStrictfp() {
        return (access & ACC_STRICT) != 0;
    }

    public boolean isSynthetic() {
        return (access & ACC_SYNTHETIC) != 0;
    }

    public boolean isAnnotation() {
        return (access & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (access & ACC_ENUM) != 0;
    }

    public boolean isSuper() {
        return (access & ACC_SUPER) != 0;
    }

    public boolean isBridge() {
        return (access & ACC_BRIDGE) != 0;
    }

    public boolean isVarargs() {
        return (access & ACC_VARARGS) != 0;
    }

    public void setPublic(final boolean value) {
        this.access = value ? this.access | ACC_PUBLIC : this.access & ~ACC_PUBLIC;
    }

    public void setPrivate(final boolean value) {
        this.access = value ? this.access | ACC_PRIVATE : this.access & ~ACC_PRIVATE;
    }

    public void setProtected(final boolean value) {
        this.access = value ? this.access | ACC_PROTECTED : this.access & ~ACC_PROTECTED;
    }

    public void setStatic(final boolean value) {
        this.access = value ? this.access | ACC_STATIC : this.access & ~ACC_STATIC;
    }

    public void setFinal(final boolean value) {
        this.access = value ? this.access | ACC_FINAL : this.access & ~ACC_FINAL;
    }

    public void setSynchronized(final boolean value) {
        this.access = value ? this.access | ACC_SYNCHRONIZED : this.access & ~ACC_SYNCHRONIZED;
    }

    public void setVolatile(final boolean value) {
        this.access = value ? this.access | ACC_VOLATILE : this.access & ~ACC_VOLATILE;
    }

    public void setTransient(final boolean value) {
        this.access = value ? this.access | ACC_TRANSIENT : this.access & ~ACC_TRANSIENT;
    }

    public void setNative(final boolean value) {
        this.access = value ? this.access | ACC_NATIVE : this.access & ~ACC_NATIVE;
    }

    public void setInterface(final boolean value) {
        this.access = value ? this.access | ACC_INTERFACE : this.access & ~ACC_INTERFACE;
    }

    public void setAbstract(final boolean value) {
        this.access = value ? this.access | ACC_ABSTRACT : this.access & ~ACC_ABSTRACT;
    }

    public void setStrictfp(final boolean value) {
        this.access = value ? this.access | ACC_STRICT : this.access & ~ACC_STRICT;
    }

    public void setSynthetic(final boolean value) {
        this.access = value ? this.access | ACC_SYNTHETIC : this.access & ~ACC_SYNTHETIC;
    }

    public void setAnnotation(final boolean value) {
        this.access = value ? this.access | ACC_ANNOTATION : this.access & ~ACC_ANNOTATION;
    }

    public void setEnum(final boolean value) {
        this.access = value ? this.access | ACC_ENUM : this.access & ~ACC_ENUM;
    }

    public void setSuper(final boolean value) {
        this.access = value ? this.access | ACC_SUPER : this.access & ~ACC_SUPER;
    }

    public void setBridge(final boolean value) {
        this.access = value ? this.access | ACC_BRIDGE : this.access & ~ACC_BRIDGE;
    }

    public void setVarargs(final boolean value) {
        this.access = value ? this.access | ACC_VARARGS : this.access & ~ACC_VARARGS;
    }
}