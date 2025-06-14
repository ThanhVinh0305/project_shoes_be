package vn.shoestore.shared.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtils {
    public static <T> List<T> diff(List<T> l, List<T> l2) {
        return l.stream().filter(element -> !l2.contains(element)).collect(Collectors.toList());
    }

    public static <T> List<T> merge(List<T> l, List<T> l2) {
        return Stream.of(l, l2).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    public static <T> List<T> intersect(List<T> l, List<T> l2) {

        return l.stream().filter(l2::contains).collect(Collectors.toList());
    }

    public static boolean equalLists(List<Long> one, List<Long> two) {
        if (one == null && two == null) {
            return true;
        }

        if (one == null || two == null || one.size() != two.size()) {
            return false;
        }
        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    public static <T> List<T> merge(List<List<T>> source) {
        return source.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static <T> List<T> mergeWithoutDistinct(List<T> l, List<T> l2) {
        return Stream.of(l, l2).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> List<T> paginate(List<T> list, int page, int size) {
        if (list == null || list.isEmpty() || page <= 0 || size <= 0) {
            return Collections.emptyList();
        }

        int fromIndex = (page - 1) * size;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + size, list.size());
        return list.subList(fromIndex, toIndex);
    }
}
