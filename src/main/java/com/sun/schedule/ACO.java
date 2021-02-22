package com.sun.schedule;

import com.sun.init.Data;
import com.sun.pojo.Ant;
import com.sun.solution.Solution;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ACO {
    private Ant[] ants; // 蚂蚁
    private int antNum = 5; // 蚂蚁数量
    private int taskNum; // 任务数量
    private int MAX_GEN = 200; // 运行代数
    private float[][] pheromone; // 信息素矩阵

    private double bestTime; // 最佳长度
    private Solution bestSolution; // 最佳路径
    private Map<Integer, Integer> map = new HashMap<>();

    // 参数
    private float alpha = .1f;
    private float rho = .5f;

    public ACO() {
        this.taskNum = Data.TASK_NUM;
        this.ants = new Ant[antNum];
        this.bestSolution = new Solution();
    }

    public void init() {

        // 初始化信息矩阵
        pheromone = new float[taskNum][taskNum];
        for (int i = 0; i < taskNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                pheromone[i][j] = 0.1f;
            }
        }

        bestTime = Double.MAX_VALUE;

        // 随机放置蚂蚁
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(taskNum);
            ants[i].init(alpha, 3);
        }
    }

    public Solution solve() throws IOException, CloneNotSupportedException {
        for (int g = 0; g < MAX_GEN; g++) {
            // antNum只蚂蚁
            for (int i = 0; i < antNum; i++) {
                // i这只蚂蚁走taskNum步，完整一个路线
                for (int j = 1; j < taskNum; j++) {
                    ants[i].selectNextTask(pheromone);
                }
                if (ants[i].getConsumeTime() < bestTime) {
                    bestTime = ants[i].getConsumeTime();
                    bestSolution.setPriority(ants[i].getTaboo());
                }
            }
            updatePheromone();
            // 重新初始化蚂蚁
            for (int i = 0; i < antNum; i++) {
                ants[i].init(alpha, 3);
            }
        }
        bestSolution.getConsumeTime();
        return bestSolution;
    }

    private void updatePheromone() throws IOException, CloneNotSupportedException {
        for (int i = 0; i < taskNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                pheromone[i][j] *= (1 - rho);
            }
        }
        for (int i = 0; i < antNum; i++) {
            for (int j = 0; j < taskNum - 1; j++) {
                pheromone[ants[i].getTaboo().get(j)][ants[i].getTaboo().get(j + 1)] += 1 / (ants[i].getConsumeTime());
            }
        }
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        System.out.println("Start...");
        ACO aco = new ACO();
        aco.init();
        Solution solution = aco.solve();
        System.out.println(solution.getConsumeTime());
        System.out.println(solution);
    }
}
