package dev.name.util.collections.list;

import dev.name.util.collections.AbstractCollection;

import java.util.ArrayList;

public abstract class AbstractArrayList<T, R extends AbstractArrayList<T, R>> extends ArrayList<T> implements AbstractCollection<T, R> {}