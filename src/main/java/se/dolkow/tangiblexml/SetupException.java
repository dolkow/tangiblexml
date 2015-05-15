package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

/** Thrown if we discover a problem with your annotation setup during parser initialization. */
public class SetupException extends TangibleException {
    public SetupException(@NonNull String msg, @NonNull Exception cause) {
        super(msg, cause);
    }

    public SetupException(@NonNull String msg) {
        super(msg);
    }
}
