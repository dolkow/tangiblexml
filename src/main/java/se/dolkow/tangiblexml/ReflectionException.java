package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

/** Thrown when we fail to access something through reflection -- this shouldn't happen. */
public class ReflectionException extends TangibleException {
    public ReflectionException(@NonNull Exception cause) {
        super(cause);
    }

    public ReflectionException(@NonNull String msg, @NonNull Exception cause) {
        super(msg, cause);
    }

}
