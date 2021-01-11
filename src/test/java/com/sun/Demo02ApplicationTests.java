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
    void contextLoads() throws IOException {
        int[][] M = new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        findCircleNum(M);
    }


    public int findCircleNum(int[][] M) {
        if (M.length == 0 || M[0].length == 0) {
            return 0;
        }
        int numCity = M.length;
        int[] parent = new int[numCity];
        for (int i = 0; i < numCity; i++) {
            parent[i] = i;
        }
        for (int i = 0; i < numCity; i++) {
            for (int j = 0; j < numCity; j++) {
                if (i != j && M[i][j] == 1) {
                    union(i, j, parent);
                }
            }
        }
        int circle = 0;
        for (int i = 0; i < numCity; i++) {
            if (parent[i] == i) {
                ++circle;
            }
        }
        return circle;
    }

    public void union(int index1, int index2, int[] parent) {
        parent[findParent(parent, index1)] = findParent(parent, index2);
    }

    public int findParent(int[] parent, int index) {
        if (parent[index] != index) {
            parent[index] = findParent(parent, parent[index]);
        }
        return parent[index];
    }
}


