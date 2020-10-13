package com.sun.pojo;

import com.sun.collision_dec.Collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ant implements Cloneable {
    private List<Integer> taboo; // 禁忌表
    private List<Integer> allowed; // 允许搜索的位置

    private float alpha;
    private float beta;

    private double consumeTime;
    private int taskNum;
    private int firstTask;
    private int currentTask;

    public Ant(int taskNum) {
        this.taskNum = taskNum;
    }

    // 初始化蚂蚁, 并随机选择初始位置
    public void init(float alpha, float beta) {
        this.alpha = alpha;
        this.beta = beta;
        this.consumeTime = 0;

        allowed = new ArrayList<>();
        taboo = new ArrayList<>();
        for (int i = 0; i < taskNum; i++) {
            Integer integer = i;
            allowed.add(integer);
        }
        // 随机挑选一个城市作为起始城市
        Random random = new Random();
        firstTask = random.nextInt(taskNum);
        for (Integer i : allowed) {
            if (firstTask == i) {
                allowed.remove(i);
                break;
            }
        }
        taboo.add(firstTask);
        currentTask = firstTask;
    }

    /**
     * 选择下一个城市
     *
     * @param pheromone 信息素矩阵
     */

    public void selectNextTask(float[][] pheromone) {
        float[] p = new float[taskNum];
        float sum = 0.0f;
        for (Integer i : allowed) {
            sum += Math.pow(pheromone[currentTask][i], alpha);
        }
        for (Integer i : allowed) {
            p[i] = (float) (Math.pow(pheromone[currentTask][i], alpha) / sum);
        }

        // 轮盘赌
        Random random = new Random();
        float selectP = random.nextFloat();
        int selectTask = 0;
        float sum1 = 0.f;
        for (int i = 0; i < taskNum; i++) {
            sum1 += p[i];
            if (sum1 > selectP) {
                selectTask = i;
                break;
            }
        }
        allowed.remove((Integer) selectTask);
        taboo.add(selectTask);
        currentTask = selectTask;
    }

    private double calculateConsumeTime() {
        if (taboo.size() == taskNum) {
            return new Collision().calRunningTime(taboo, false);
        } else
            return 0;
    }

    public double getConsumeTime() {
        if ((Math.abs(this.consumeTime) < 1e-6)) {
            this.consumeTime = calculateConsumeTime();
        }
        return this.consumeTime;
    }

    public List<Integer> getTaboo() {
        return taboo;
    }

    public int getFirstTask() {
        return firstTask;
    }
}
