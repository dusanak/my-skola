package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    @Test
    void antiNegativeSort() {
        App app = new App();
        List<Integer> testList = Arrays.asList(10, 9, 4, -16, 7, -3);

        List<Integer> result = app.antiNegativeSort(testList);

        assertThat(result, allOf(
                not(hasItem(lessThan(0))),
                new IsListSorted()
                )
        );
        assertEquals(4, result.size());
    }
}
