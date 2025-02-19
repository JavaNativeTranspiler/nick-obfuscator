package dev.name.transpiler.types;

public enum Type {
    BOOLEAN("z"),
    BYTE("b"),
    SHORT("s"),
    CHAR("c"),
    INT("i"),
    OBJECT("l"),
    LONG("j"),
    DOUBLE("d"),
    FLOAT("f");

    public final String id;

    Type(final String id) {
        this.id = id;
    }
}