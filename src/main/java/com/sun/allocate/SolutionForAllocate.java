package com.sun.allocate;

import com.sun.collision_dec.Collision;

import java.io.IOException;
import java.util.*;

public class SolutionForAllocate {
    private List<Integer> allocationNo;

    private double consumeTime;

    public SolutionForAllocate() {
        allocationNo = new ArrayList<>();
        consumeTime = 0.0;
    }

    public SolutionForAllocate(List<Integer> allocationNo) {
        this.allocationNo = allocationNo;
    }

    public List<Integer> getAllocationNo() {
        return allocationNo;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "allocationNo=" + allocationNo +
                ", consumeTime=" + consumeTime +
                '}';
    }

    public SolutionForAllocate clone() {
        SolutionForAllocate solution_copy = new SolutionForAllocate();
        solution_copy.allocationNo.addAll(this.allocationNo);
        return solution_copy;
    }

    public void setAllocationNo(int allocationIndex, int i) {
        this.allocationNo.set(allocationIndex, i);
        this.consumeTime = 0;
    }

    public SolutionForAllocate addAllocationNo(Integer pri) {
        this.allocationNo.add(pri);
        return this;
    }

    public double getConsumeTime() throws IOException, CloneNotSupportedException {
        if (Math.abs(consumeTime) <= 1e-6) {
            consumeTime = new Collision().calRunningTime(this.allocationNo, false);
        }
        return consumeTime;
    }

    /**
     * For 模拟退火
     */
    public SolutionForAllocate genIndividual(List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        for (int i = 0; i < 3; i++) {
            int count = countForEveryType.get(i);
            List<Integer> availableList = availableStock.get(i);
            Set<Integer> selectedList = new TreeSet<>(Comparator.comparingInt(e -> e));
            Random random = new Random();
            int index = 0;
            while (true) {
                index = random.nextInt(availableList.size());
                selectedList.add(availableList.get(index));
                if (selectedList.size() >= count) {
                    break;
                }
            }
            this.allocationNo.addAll(selectedList);
        }
        return this;
    }

    public SolutionForAllocate generateNext(List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        SolutionForAllocate newSolution = this.clone();
        int begin = 0;
        for (int i = 0; i < 3; i++) {
            int count = countForEveryType.get(i);
            List<Integer> availableList = availableStock.get(i);
            List<Integer> selectedList = this.allocationNo.subList(begin, begin + count);
            if (count == availableList.size()) {
                continue;
            }
            List<Integer> unSelectedList = new ArrayList<>();
            for (Integer stock : availableList) {
                if (!selectedList.contains(stock)) {
                    unSelectedList.add(stock);
                }
            }
            Random random = new Random();
            int index1 = random.nextInt(unSelectedList.size());
            int index2 = random.nextInt(selectedList.size());
            newSolution.setAllocationNo(begin+index2, unSelectedList.get(index1));
            begin += count;

        }

        return newSolution;
    }
}
