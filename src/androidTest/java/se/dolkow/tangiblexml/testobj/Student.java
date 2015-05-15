package se.dolkow.tangiblexml.testobj;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import se.dolkow.tangiblexml.TangibleField;

public class Student extends Person{

    @SuppressWarnings("unused")
    @TangibleField(value = "Id", required = false)
    @Nullable
    public Integer id;

    @SuppressWarnings("unused")
    @TangibleField("CourseGrade/Name")
    @Nullable
    public ArrayList<String> courses;

    @SuppressWarnings("unused")
    @TangibleField(value = "CourseGrade/Grade", required = false)
    @Nullable
    public ArrayList<String> grades;
}
