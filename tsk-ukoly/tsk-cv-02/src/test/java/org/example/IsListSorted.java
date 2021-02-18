package org.example;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IsListSorted extends BaseMatcher<List<Integer>> {
    @Override
    public boolean matches(Object o) {
        List<Integer> list = (ArrayList<Integer>) o;
        for (int i = 0; i < list.size() - 1; i++ ) {
            if (list.get(i) > list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {

    }
}
