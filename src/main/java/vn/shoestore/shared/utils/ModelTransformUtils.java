package vn.shoestore.shared.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ModelTransformUtils {

  private ModelTransformUtils() {}

  /**
   * Tạo ra một map với key được chỉ định và value là list các object trong list có thuộc tính key
   * đó
   */
  public static <T, K> Map<K, List<T>> toMapList(
      List<T> source, Function<? super T, ? extends K> key) {
    return source.stream().collect(groupingBy(key));
  }

  /**
   * Từ 1 list chuyển thành một map với key vaf value là các attribute của object và nêú trong list
   * có duplicate key thì lấy key đầu tiên (mảng phải được order theo mục đích của logic)
   */
  public static <T, K, V> Map<K, V> toMap(
      List<T> source,
      Function<? super T, ? extends K> key,
      Function<? super T, ? extends V> value) {
    return source.stream().collect(Collectors.toMap(key, value, (first, second) -> first));
  }

  /** Từ một list object chuyển thành một map key object với key được truyền vào */
  public static <T, K> Map<K, T> toMap(List<T> source, Function<? super T, ? extends K> key) {

    return source.stream()
        .collect(Collectors.toMap(key, Function.identity(), (first, second) -> second));
  }

  public static <T, V> List<V> getAttribute(
      List<T> source, Function<? super T, ? extends V> attribute) {
    return source.stream().map(attribute).collect(Collectors.toList());
  }
}
