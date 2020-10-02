package com.highestpeak.springblog.util;

import java.util.*;

/**
 * @author highestpeak
 */
public class BinarySearches {

    private static final int BINARYSEARCH_THRESHOLD = 5000;

    /**
     * 复制的JDK的 BinarySearch
     * 目前只针对 ArrayList
     */
    public static <T>
    boolean checkParam(List<? extends T> list, T key, Comparator<? super T> c) {
        return c != null && (list instanceof ArrayList || list.size() < BINARYSEARCH_THRESHOLD);
    }

    /**
     * 查找一个值的列表，所有重复值都要找到
     *
     * @param list the list to be searched.
     * @param key  the key to be searched for.
     * @param <T>  the class of the objects in the list
     * @return the indexes of the search key,
     */
    public static <T>
    List<T> binarySearchDuplicate(List<T> list, T key, Comparator<T> c) {
        if (checkParam(list, key, c)) {
            throw new UnsupportedOperationException();
        }
        int leftIndex = binarySearchLeftBound(list, key, c);
        int rightIndex = binarySearchRightBound(list, key, c);
        return list.subList(leftIndex, rightIndex + 1);
    }

    /**
     * 左侧边界二分查找
     */
    public static <T>
    int binarySearchLeftBound(List<T> list, T key, Comparator<T> c) {
        if (checkParam(list, key, c)) {
            throw new UnsupportedOperationException();
        }

        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = list.get(mid);
            int cmp = c.compare(midVal, key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                high = mid - 1; // key found
            }
        }

        return low;
    }

    /**
     * 右侧边界二分查找
     */
    public static <T>
    int binarySearchRightBound(List<T> list, T key, Comparator<T> c) {
        if (checkParam(list, key, c)) {
            throw new UnsupportedOperationException();
        }

        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = list.get(mid);
            int cmp = c.compare(midVal, key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                low = mid + 1; // key found
            }
        }

        return low;
    }
}
