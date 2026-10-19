package com.wang.tradingplatform.utils;

import java.util.Map;
import java.util.Set;

public class ListUtil {
    //传递的集合A是否有元素存在于集合B Set集合版本
    public static boolean ListAIfListB(Set<String> A, Set<String> B) {
        int live = 0;
        for (String a : A) {
            for (String b : B) {
                if (a.equals(b)) {
                    live++;
                }
            }
        }
        return live != 0;
    }

    //传递的集合A是否有元素存在于集合B Map集合版本 如果有返回对应的id没有就返回null
    public static Integer ListAIfListB(Set<String> A, Map<Integer, String> B) {
        for (String a : A) {
            for (Map.Entry<Integer, String> entry : B.entrySet()) {
                if (a.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
