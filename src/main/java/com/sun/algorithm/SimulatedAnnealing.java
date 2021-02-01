package com.sun.algorithm;

import com.sun.allocate.TestData;
import com.sun.collision_dec.Collision;
import com.sun.data.Data;
import com.sun.pojo.Solution;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SimulatedAnnealing {
    // 初始温度
    private double currentTemperature = 5000;
    // 最低温度
    private final double minTemperature = 1;
    // 某一温度下迭代次数
    private final int internalLoop = 10;
    // 冷却比率
    private final double coolingRate = 0.2;
    // 初始解
    private Solution currentSolution;

    public void initSolution() {
        currentTemperature = 5000;
        Solution solution = Data.solution;
        currentSolution = solution.genIndividual();
        //System.out.println("最初距离：" + currentSolution.getConsumeTime());
    }

    public Solution anneal(List<Integer> allocationNo) throws IOException, CloneNotSupportedException {
        Solution bestSolution;
        Solution newSolution = null;
        if (allocationNo == null) {
            bestSolution = new Solution(currentSolution.getPriority());
        } else {
            currentSolution.setAllocationNo(allocationNo);
            bestSolution = new Solution(currentSolution.getPriority(), allocationNo);
        }

        while (currentTemperature > minTemperature) {
            for (int i = 0; i < internalLoop; i++) {
                newSolution = currentSolution.generateNext();
                double currentEnergy = currentSolution.getConsumeTime();
                double newEnergy = newSolution.getConsumeTime();

                if (acceptProbability(currentEnergy, newEnergy) > Math.random()) {
                    currentSolution = newSolution.clone();
                    if (currentSolution.getConsumeTime() < bestSolution.getConsumeTime()) {
                        bestSolution = currentSolution.clone();
                    }
                }
            }
            currentTemperature *= (1 - coolingRate);
        }
        return bestSolution;
    }

    private double acceptProbability(double currentEnergy, double newEnergy) {
        if (newEnergy < currentEnergy)
            return 1.1;
        return Math.exp(-(newEnergy - currentEnergy) / currentTemperature);
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        SimulatedAnnealing sa = new SimulatedAnnealing();
        TestData testData = new TestData();
        testData.initSolution(9);
        sa.initSolution();
        List<Integer> allocationNo = Arrays.asList(4, 1, 9, 8, 7, 11, 13, 10, 14);
        //Solution best = sa.anneal(allocationNo);
        Solution best = new Solution();
        best.setAllocationNo(allocationNo);
        best.setPriority(Arrays.asList(5, 2, 0, 6, 4, 1, 7, 8, 3));
        System.out.println(best);
        System.out.println("最短时间：" + best.getConsumeTime());

        testData.initSolution(9);
        sa.initSolution();
        testData.clearTaskMap();
        List<Integer> allocationNo2 = Arrays.asList(3, 0, 9, 5, 8, 10, 13, 11, 14);
        best.setAllocationNo(allocationNo2);
        best.setPriority(Arrays.asList(6, 3, 1, 7, 2, 0, 5, 8, 4));
        System.out.println(best);
        System.out.println("最短时间：" + best.getConsumeTime());

    }
}
