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
        int[][] edges = {{2, 1}, {3, 1}, {4, 2}, {1, 4}};
        findRedundantDirectedConnection(edges);
    }

    public int[] findRedundantDirectedConnection(int[][] edges) {
        int len = edges.length;

        UnionFind uf = new UnionFind(len);
        int[] parent = new int[len];
        for (int i = 0; i < len; i++) {
            parent[i] = i;
        }

        int conflict = -1;
        int cycle = -1;

        for (int i = 0; i < len; i++) {
            int[] edge = edges[i];
            int u = edge[0] - 1;
            int v = edge[1] - 1;
            if (parent[v] != v) {
                conflict = i;
            } else {
                parent[v] = u;
                if (uf.find(u) == uf.find(v)) {
                    cycle = i;
                } else {
                    uf.union(u, v);
                }
            }

        }
        if (conflict < 0) {
            return edges[cycle];
        } else {
            int[] conflictEdge = edges[conflict];
            if (cycle >= 0) {
                int[] res = new int[]{parent[conflictEdge[1]], conflictEdge[1]};
                return res;
            } else {
                return conflictEdge;
            }
        }

    }

    private class UnionFind {
        private int[] rank;
        private int[] parent;

        public UnionFind(int n) {
            rank = new int[n];
            parent = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 1;
            }
        }

        public int find(int x) {
            if (x != parent[x]) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            x = find(x);
            y = find(y);
            if (rank[x] == rank[y]) {
                parent[x] = y;
                ++rank[y];
            } else if (rank[x] < rank[y]) {
                parent[x] = y;
            } else {
                parent[y] = x;
            }
        }
    }
}


