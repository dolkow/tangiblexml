package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

/** Thrown when we fail to convert some input text into the expected type (like an int). */
public class ConversionException extends TangibleException {
    public ConversionException(@NonNull Exception cause) {
        super(cause);
    }
}
