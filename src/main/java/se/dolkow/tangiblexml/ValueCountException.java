package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

/** Thrown when we discover that there are too many or too few values */
public class ValueCountException extends TangibleException {
    public ValueCountException(@NonNull String msg) {
        super(msg);
    }
}
