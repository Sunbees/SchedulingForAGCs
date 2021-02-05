package com.sun;

import com.sun.collision_dec.Collision;
import com.sun.collision_dec.Crane;
import com.sun.collision_dec.Location;
import com.sun.collision_dec.Task;
import com.sun.data.Data;
import com.sun.pojo.Order;
import com.sun.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

@SpringBootTest
class Demo02ApplicationTests {

    @Test
    public void test() {
        equalSubstring("krrgw", "zjxss", 19);

    }

    public int equalSubstring(String s, String t, int maxCost) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(e -> Math.abs(s.charAt(e) - t.charAt(e))));
        int n = s.length();
        for (int i = 0; i < n; i++) {
            pq.offer(i);
        }
        int count = 0;
        while (!pq.isEmpty()) {
            int index = pq.poll();
            if (maxCost >= Math.abs(s.charAt(index) - t.charAt(index))) {
                ++count;
                maxCost -= Math.abs(s.charAt(index) - t.charAt(index));
            } else {
                return count;
            }
        }
        return n;

    }
}


