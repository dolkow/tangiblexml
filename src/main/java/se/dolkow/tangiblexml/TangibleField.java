package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TangibleField {
    /**
     * Relative xml path from its parent node.
     */
    @NonNull String value();

    /**
     * We will throw exceptions if a required field is missing.
     * For lists, "required" means that we require at least one element.
     * Default is true.
     */
    boolean required() default true;
}
