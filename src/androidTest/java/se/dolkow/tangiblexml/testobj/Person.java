package se.dolkow.tangiblexml.testobj;

import android.support.annotation.Nullable;

import se.dolkow.tangiblexml.TangibleField;

public class Person {
    @SuppressWarnings("unused")
    @TangibleField("Name")
    @Nullable
    public String name;
}
