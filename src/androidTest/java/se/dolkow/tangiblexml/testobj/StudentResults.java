package se.dolkow.tangiblexml.testobj;

import android.support.annotation.Nullable;

import java.util.LinkedList;

import se.dolkow.tangiblexml.TangibleField;
import se.dolkow.tangiblexml.TangibleRoot;

@TangibleRoot("/StudentRegistry/StudentList")
public class StudentResults {
    @SuppressWarnings("unused")
    @TangibleField(value = "Student", required = false)
    @Nullable
    public LinkedList<Student> students;
}
