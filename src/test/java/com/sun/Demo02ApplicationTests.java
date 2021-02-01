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
        int[][] grid = new int[][]{{0,1,2,3,4}, {24,23,22,21,5}, {12,13,14,15,16}, {11,17,18,19,20}, {10,9,8,7,6}};
        Solution s1 = new Solution();
        s1.swimInWater(grid);

    }


    public class Solution {

        // Dijkstra 算法（应用前提：没有负权边，找单源最短路径）

        public int swimInWater(int[][] grid) {
            int n = grid.length;

            Queue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(o -> grid[o[0]][o[1]]));
            minHeap.offer(new int[]{0, 0});

            boolean[][] visited = new boolean[n][n];
            // distTo[i][j] 表示：到顶点 [i, j] 须要等待的最少的时间
            int[][] distTo = new int[n][n];
            for (int[] row : distTo) {
                Arrays.fill(row, n * n);
            }
            distTo[0][0] = grid[0][0];

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            while (!minHeap.isEmpty()) {
                // 找最短的边
                int[] front = minHeap.poll();
                int currentX = front[0];
                int currentY = front[1];
                if (visited[currentX][currentY]) {
                    continue;
                }

                // 确定最短路径顶点
                visited[currentX][currentY] = true;
                if (currentX == n - 1 && currentY == n - 1) {
                    return distTo[n - 1][n - 1];
                }

                // 更新
                for (int[] direction : directions) {
                    int newX = currentX + direction[0];
                    int newY = currentY + direction[1];
                    if (inArea(newX, newY, n) && !visited[newX][newY] &&
                            Math.max(distTo[currentX][currentY], grid[newX][newY]) < distTo[newX][newY]) {
                        distTo[newX][newY] = Math.max(distTo[currentX][currentY], grid[newX][newY]);
                        minHeap.offer(new int[]{newX, newY});
                    }
                }
            }
            return -1;
        }

        private boolean inArea(int x, int y, int n) {
            return x >= 0 && x < n && y >= 0 && y < n;
        }
    }
}


