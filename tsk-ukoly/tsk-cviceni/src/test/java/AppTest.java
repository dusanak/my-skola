import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    boolean isSorted(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    @Test
    void bubbleSortPresorted() {
        List<Integer> list = new ArrayList<Integer>();

        list.add(1);
        list.add(5);
        list.add(10);

        App.bubbleSort(list);

        assertTrue(isSorted(list));
    }

    @Test
    void bubbleSortUnsorted() {
        List<Integer> list = new ArrayList<Integer>();

        list.add(10);
        list.add(5);
        list.add(1);

        App.bubbleSort(list);

        assertTrue(isSorted(list));
    }
}