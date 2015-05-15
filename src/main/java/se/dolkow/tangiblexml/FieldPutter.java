package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

class FieldPutter<Target,Value> implements Putter<Target,Value> {

    private final @NonNull Field where;

    FieldPutter(@NonNull Field where) {
        this.where = where;
    }

    @Override
    public void put(@NonNull Target target, @NonNull Value value)
            throws ReflectionException, ValueCountException {
        if (get(target) != null) {
            throw new ValueCountException("Field " + where + " has already been set!");
        }
        try {
            where.set(target, value);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public @Nullable Value get(@NonNull Target target) throws ReflectionException {
        try {
            //noinspection unchecked
            return (Value)where.get(target);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }
}
