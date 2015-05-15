package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

/** For internal use. We called a function that was not supported. */
class NotSupportedException extends TangibleException {
    public NotSupportedException(@NonNull String msg) {
        super(msg);
    }
}
