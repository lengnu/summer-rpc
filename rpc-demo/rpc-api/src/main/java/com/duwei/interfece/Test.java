package com.duwei.interfece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 21:32
 * @since: 1.0
 */
public class Test {
    public static void main(String[] args) {
        List<Integer> a = new ArrayList<>(Arrays.asList(1,2,3,4));
        List<Integer> b = new ArrayList<>(Arrays.asList(2,3,5));
        Iterator<Integer> iterator = a.iterator();
        System.out.println(iterator.next());
        System.out.println(iterator.next());
        System.out.println(iterator.next());
        System.out.println(iterator.next());
        String  aa= null;
        System.out.println(aa.hashCode());
    }
}
