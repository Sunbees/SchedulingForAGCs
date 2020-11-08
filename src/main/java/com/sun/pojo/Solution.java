package com.sun.pojo;

import com.sun.collision_dec.Collision;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
    private List<Integer> priority;

    private double consumeTime;

    public Solution() {
        priority = new ArrayList<>();
        consumeTime = 0.0;
    }

    public Solution(List<Integer> priority) {
        this.priority = priority;
    }

    public List<Integer> getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "priority=" + priority +
                ", consumeTime=" + consumeTime +
                '}';
    }

    public Solution clone() {
        Solution solution_copy = new Solution();
        solution_copy.priority.addAll(this.priority);
        return solution_copy;
    }

    public void setPriority(int priorityIndex, int i) {
        this.priority.set(priorityIndex, i);
        this.consumeTime = 0;
    }

    public void setPriority(List<Integer> priority) {
        this.priority = priority;
    }

    public void setConsumeTime(double consumeTime) {
        this.consumeTime = consumeTime;
    }

    public Integer getPriority(int priorityIndex) {
        return this.priority.get(priorityIndex);
    }

    public int numOfTask() {
        return this.priority.size();
    }

    public Solution addPriority(Integer pri) {
        this.priority.add(pri);
        return this;
    }

    public double getConsumeTime() throws IOException, CloneNotSupportedException {
        if (Math.abs(consumeTime) <= 1e-6) {
            consumeTime = new Collision().calRunningTime(this.priority, false);
        }
        return consumeTime;
    }

    /**
     * For 模拟退火
     */
    public Solution genIndividual() {
        for (int priorityIndex = 0; priorityIndex < priority.size(); priorityIndex++) {
            setPriority(priorityIndex, this.getPriority(priorityIndex));
        }
        Collections.shuffle(this.priority);
        return this;
    }

    public Solution generateNext() {
        Solution newRoute = this.clone();

        int pos1 = (int) (newRoute.numOfTask() * Math.random());
        int pos2 = (int) (newRoute.numOfTask() * Math.random());

        if (pos1 != pos2) {
            Integer swap1 = newRoute.getPriority(pos1);
            Integer swap2 = newRoute.getPriority(pos2);

            newRoute.setPriority(pos1, swap2);
            newRoute.setPriority(pos2, swap1);
        }
        return newRoute;
    }

    /**
     * For 遗传算法
     * */
    private double rate;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void createRandomly(List<Integer> priority) {
        List<Integer> list = new ArrayList<>(priority);
        Collections.shuffle(list);
        this.priority = list;
        this.consumeTime = 0.0;
    }
}
