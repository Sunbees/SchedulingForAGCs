package com.sun.pojo;

import com.sun.allocate.SolutionForAllocate;
import com.sun.collision_dec.Collision;
import lombok.SneakyThrows;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.*;

public class Solution {
    private List<Integer> priority;

    private double consumeTime;

    private boolean isAllocate;

    private List<Set<Integer>> allocationNo;


    public Solution() {
        priority = new ArrayList<>();
        allocationNo = new ArrayList<>();
        consumeTime = 0.0;
        isAllocate = false;
    }

    public Solution(List<Integer> priority) {
        this.priority = priority;
    }

    public Solution(List<Integer> priority, List<Set<Integer>> allocationNo) {
        this.priority = priority;
        this.allocationNo = allocationNo;
        this.isAllocate = true;
    }

    public List<Integer> getPriority() {
        return priority;
    }

    public List<Set<Integer>> getAllocationNo() {
        return this.allocationNo;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "priority=" + priority +
                ", consumeTime=" + consumeTime +
                ", allocationNo=" + allocationNo +
                '}';
    }

    @SneakyThrows
    public Solution clone() {
        Solution solution_copy = new Solution();
        solution_copy.priority = new ArrayList<>(this.priority);
        if (isAllocate) {
            solution_copy.isAllocate = true;
            solution_copy.allocationNo = new ArrayList<>();
            for (Set<Integer> set : this.allocationNo) {
                solution_copy.allocationNo.add(new HashSet<>(set));
            }
        }
        solution_copy.consumeTime = this.getConsumeTime();
        return solution_copy;
    }

    public void setPriority(int priorityIndex, int i) {
        this.priority.set(priorityIndex, i);
        this.consumeTime = 0;
    }

    public void setAllocationNo(List<Set<Integer>> allocationNo) {
        this.allocationNo = allocationNo;
        this.isAllocate = true;
    }

    public void setPriority(List<Integer> priority) {
        this.priority = priority;
        this.consumeTime = 0;
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
        this.consumeTime = 0;
        return this;
    }

    public double getConsumeTime() throws IOException {
        if (Math.abs(consumeTime) <= 1e-6) {
            if (!isAllocate) {
                consumeTime = new Collision().calRunningTime(this.priority, false);
            } else {
                consumeTime = new Collision().calRunningTime(this.priority, false, allocationNo, allocationNo.size());
            }
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
     */
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

    public void createRandomlyForAllocate(List<Integer> priority, List<Set<Integer>> allocationNo) {
        List<Integer> list = new ArrayList<>(priority);
        Collections.shuffle(list);
        this.priority = list;
        this.allocationNo = allocationNo;
        this.isAllocate = true;
        this.consumeTime = 0.0;
    }
}
