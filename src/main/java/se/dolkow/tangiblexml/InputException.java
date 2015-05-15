package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

/** Thrown if we encounter read or parse errors from the XML pull parser. */
public class InputException extends TangibleException {
    public InputException(@NonNull Exception cause) {
        super(cause);
    }
}
