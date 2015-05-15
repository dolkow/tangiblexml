package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TangibleRoot {
    /**
     * Absolute xml path where this result is to be found.
     */
    @NonNull String value();
}
