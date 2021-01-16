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
        int[][] grid = {{1, 0, 1}, {1, 1, 1}};
        int[][] hits = {{0, 0}, {0, 2}, {1, 1}};
        int[] ans = hitBricks(grid, hits);
        for (int an : ans) {
            System.out.print(an+" ");
        }
    }

    private int rows;
    private int cols;

    public static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}};

    public int[] hitBricks(int[][] grid, int[][] hits) {
        this.rows = grid.length;
        this.cols = grid[0].length;

        int[][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = grid[i][j];
            }
        }

        for (int[] hit : hits) {
            copy[hit[0]][hit[1]] = 0;
        }
        int size = rows * cols;
        UnionFind uf = new UnionFind(size + 1);
        for (int j = 0; j < cols; j++) {
            if (copy[0][j] == 1) {
                uf.union(j, size);
            }
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (copy[i][j] == 1) {
                    if (copy[i - 1][j] == 1) {
                        uf.union(getIndex(i - 1, j), getIndex(i, j));
                    }
                    if (j > 0 && copy[i][j - 1] == 1) {
                        uf.union(getIndex(i, j - 1), getIndex(i, j));
                    }
                }
            }
        }
        int hitsLen = hits.length;
        int[] res = new int[hitsLen];
        for (int i = hitsLen - 1; i >= 0; i--) {
            int x = hits[i][0];
            int y = hits[i][1];

            if (grid[x][y] == 0) {
                continue;
            }
            copy[x][y] = 1;
            int originSize = uf.getSize(size);
            if (x == 0) {
                uf.union(y, size);
            }
            for (int[] dir : DIRECTIONS) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                if (inArea(newX, newY) && copy[newX][newY] == 1) {
                    uf.union(getIndex(x, y), getIndex(newX, newY));
                }
            }
            int currentSize = uf.getSize(size);
            res[i] = Math.max(0, currentSize - originSize - 1);
        }
        return res;
    }

    private boolean inArea(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private int getIndex(int x, int y) {
        return x * cols + y;
    }

    private class UnionFind {
        private int[] parent;
        private int[] size;

        public UnionFind(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            if (x != parent[x]) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return;
            }
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        }

        public int getSize(int x) {
            return size[find(x)];
        }
    }
}


