package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

public abstract class TangibleException extends Exception {
    public TangibleException(@NonNull Throwable cause) {
        super(cause);
    }

    public TangibleException(@NonNull String msg) {
        super(msg);
    }

    public TangibleException(@NonNull String msg, Throwable cause) {
        super(msg, cause);
    }
}
