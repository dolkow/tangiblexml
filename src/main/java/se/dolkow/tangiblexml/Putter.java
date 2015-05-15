package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


interface Putter<Target,Value> {
    void put(@NonNull Target target, @NonNull Value value)
            throws ReflectionException, ValueCountException;

    /** This method is optional. Some implementations may always throw. */
    @Nullable Value get(@NonNull Target target) throws ReflectionException, NotSupportedException;
}
