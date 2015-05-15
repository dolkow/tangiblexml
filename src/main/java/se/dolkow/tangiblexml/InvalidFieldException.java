package se.dolkow.tangiblexml;

import android.support.annotation.NonNull;

public class InvalidFieldException extends SetupException {
    public InvalidFieldException(@NonNull String msg) {
        super(msg);
    }
}
