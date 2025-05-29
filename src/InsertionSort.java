import java.util.List;
import java.util.Comparator;

public class InsertionSort {
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) {
            return;
        }
        for (int i = 1; i < list.size(); i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }
}