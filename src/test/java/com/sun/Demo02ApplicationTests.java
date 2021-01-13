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
        int n = 8, m = 2;
        int[] group = new int[]{-1, -1, 1, 0, 0, 1, 0, -1};
        List<List<Integer>> beforeItems = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            beforeItems.add(new ArrayList<>());
        }
        beforeItems.get(1).add(6);
        beforeItems.get(2).add(5);
        beforeItems.get(3).add(6);
        beforeItems.get(4).add(3);
        beforeItems.get(4).add(6);
        int[] ans = sortItems(n, m, group, beforeItems);
        for (int an : ans) {
            System.out.println(an);
        }
    }

    public int[] sortItems(int n, int m, int[] group, List<List<Integer>> beforeItems) {
        List<List<Integer>> groupItem = new ArrayList<>();
        for (int i = 0; i < n + m; i++) {
            groupItem.add(new ArrayList<>());
        }

        // 组间和组内依赖图
        List<List<Integer>> groupGraph = new ArrayList<>();
        for (int i = 0; i < n + m; i++) {
            groupGraph.add(new ArrayList<>());
        }
        List<List<Integer>> itemGraph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            itemGraph.add(new ArrayList<>());
        }

        // 组内和组间入度数组
        int[] groupDegree = new int[n + m];
        int[] itemDegree = new int[n];

        List<Integer> id = new ArrayList<>();
        for (int i = 0; i < n + m; i++) {
            id.add(i);
        }

        int leftId = m;
        for (int i = 0; i < n; i++) {
            if (group[i] == -1) {
                group[i] = leftId;
                ++leftId;
            }
            groupItem.get(group[i]).add(i);
        }
        // 依赖关系建图
        for (int i = 0; i < n; i++) {
            int curGroupId = group[i];
            for (int item : beforeItems.get(i)) {
                int beforeGroupId = group[item];
                if (beforeGroupId == curGroupId) {
                    ++itemDegree[i];
                    itemGraph.get(item).add(i);
                } else {
                    ++groupDegree[curGroupId];
                    groupGraph.get(beforeGroupId).add(curGroupId);
                }
            }
        }
        //组间拓扑关系排序
        List<Integer> groupTopSort = topSort(groupDegree, groupGraph, id);

        if (groupTopSort.size() == 0) {
            return new int[0];
        }

        int[] ans = new int[n];
        int index = 0;
        // 组内关系排序
        for (int curGroupId : groupTopSort) {
            int size = groupItem.get(curGroupId).size();
            if (size == 0) {
                continue;
            }
            List<Integer> res = topSort(itemDegree, itemGraph, groupItem.get(curGroupId));
            if (res.size() == 0) {
                return new int[0];
            }
            for (int item : res) {
                ans[index++] = item;
            }
        }
        return ans;

    }

    public List<Integer> topSort(int[] degrees, List<List<Integer>> graph, List<Integer> items) {
        Queue<Integer> queue = new LinkedList<>();
        for (int item : items) {
            if (degrees[item] == 0) {
                queue.offer(item);
            }
        }
        List<Integer> res = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            res.add(u);
            for (int v : graph.get(u)) {
                if (--degrees[v] == 0) {
                    queue.offer(v);
                }
            }
        }
        return items.size() == res.size() ? res : new ArrayList<>();
    }
}


