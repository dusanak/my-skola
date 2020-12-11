package java_example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelperFunctions {
    static Random random_generator = new Random();

    public static Integer randomEvenInt(int range) {
        // if a number is odd, return null
        int random_number = random_generator.nextInt(range);

        if (random_number % 2 == 0) {
            return random_number;
        }

        return null;
    }

    public static List<Integer> randomEvenIntList(int range, int size) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            result.add(randomEvenInt(range));
        }

        return result;
    }
}
