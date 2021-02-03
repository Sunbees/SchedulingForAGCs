package com.sun.algorithm;

import com.sun.allocate.TestData;
import com.sun.collision_dec.Collision;
import com.sun.data.Data;
import com.sun.pojo.Solution;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.*;

@Service
public class SimulatedAnnealing {
    // 初始解
    private Solution currentSolution;

    public void initSolution() {
        Solution solution = Data.solution;
        currentSolution = solution.genIndividual();
        //System.out.println("最初距离：" + currentSolution.getConsumeTime());
    }

    public Solution anneal(List<Set<Integer>> allocationNo, double currentTemperature, double minTemperature, double coolingRate, int internalLoop) throws IOException {
        Solution bestSolution;
        Solution newSolution = null;
        if (allocationNo == null) {
            bestSolution = new Solution(currentSolution.getPriority());
        } else {
            currentSolution.setAllocationNo(allocationNo);
            bestSolution = new Solution(currentSolution.getPriority(), allocationNo);
        }

        // 最低温度
        while (currentTemperature > minTemperature) {
            // 某一温度下迭代次数
            for (int i = 0; i < internalLoop; i++) {
                newSolution = currentSolution.generateNext();
                double currentEnergy = currentSolution.getConsumeTime();
                double newEnergy = newSolution.getConsumeTime();

                if (acceptProbability(currentEnergy, newEnergy, currentTemperature) > Math.random()) {
                    currentSolution = newSolution.clone();
                    if (currentSolution.getConsumeTime() < bestSolution.getConsumeTime()) {
                        bestSolution = currentSolution.clone();
                    }
                }
            }
            // 冷却比率
            currentTemperature *= (1 - coolingRate);
        }
        return bestSolution;
    }

    private double acceptProbability(double currentEnergy, double newEnergy, double currentTemperature) {
        if (newEnergy < currentEnergy)
            return 1.1;
        return Math.exp(-(newEnergy - currentEnergy) / currentTemperature);
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        SimulatedAnnealing sa = new SimulatedAnnealing();
        TestData.initSolution(9);
        sa.initSolution();
        Set<Integer> set1 = new HashSet<>();
        set1.add(4);
        set1.add(1);
        Set<Integer> set2 = new HashSet<>();
        set2.add(9);
        set2.add(8);
        set2.add(7);
        Set<Integer> set3 = new HashSet<>();
        set3.add(10);
        set3.add(11);
        set3.add(13);
        set3.add(14);
        List<Set<Integer>> allocationNo = Arrays.asList(set1, set2, set3);
        Solution best = sa.anneal(allocationNo, 10000, 0.1, 0.1, 200);
        //Solution best = new Solution();
        //best.setAllocationNo(allocationNo);
        //best.setPriority(Arrays.asList(5, 2, 0, 6, 4, 1, 7, 8, 3));
        System.out.println(best);
        System.out.println("最短时间：" + best.getConsumeTime());


        set1 = new HashSet<>();
        set1.add(2);
        set1.add(1);
        set2 = new HashSet<>();
        set2.add(9);
        set2.add(8);
        set2.add(5);
        set3 = new HashSet<>();
        set3.add(10);
        set3.add(12);
        set3.add(13);
        set3.add(14);

        TestData.initSolution(9);
        sa.initSolution();
        TestData.clearTaskMap();
        List<Set<Integer>> allocationNo2 = Arrays.asList(set1, set2, set3);
        best = sa.anneal(allocationNo2, 10000, 0.1, 0.2, 200);
        //best.setAllocationNo(allocationNo2);
        //best.setPriority(Arrays.asList(6, 3, 1, 7, 2, 0, 5, 8, 4));
        System.out.println(best);
        System.out.println("最短时间：" + best.getConsumeTime());

    }
}
