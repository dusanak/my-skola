import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    public static void bubbleSort (List < Integer > list) {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i) > list.get(i + 1)) {
                    int tmp = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, tmp);

                    flag = true;
                }
            }
        }
    }
}
