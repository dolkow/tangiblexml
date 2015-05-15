package se.dolkow.tangiblexml;

import android.os.Build;
import android.support.annotation.NonNull;

final class Trace {
    public static void beginSection(@NonNull String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            android.os.Trace.beginSection(s);
        }
    }

    public static void endSection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            android.os.Trace.endSection();
        }
    }
}
