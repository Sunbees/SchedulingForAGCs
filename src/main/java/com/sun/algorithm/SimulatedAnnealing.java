package com.sun.algorithm;

import com.sun.collision_dec.Collision;
import com.sun.data.Data;
import com.sun.pojo.Solution;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SimulatedAnnealing {
    private static Collision collision = new Collision();;
    // 初始温度
    private double currentTemperature = 5000;
    // 最低温度
    private final double minTemperature = 0.1;
    // 某一温度下迭代次数
    private final int internalLoop = 10;
    // 冷却比率
    private final double coolingRate = 0.5;
    // 初始解
    private Solution currentSolution;

    public void initSolution() {
        Solution solution = Data.solution;
        currentSolution = solution.genIndividual();
        //System.out.println("最初距离：" + currentSolution.getConsumeTime());
    }

    public Solution anneal() throws IOException, CloneNotSupportedException {
        Solution bestSolution = new Solution(currentSolution.getPriority());
        Solution newSolution = null;

        while (currentTemperature > minTemperature) {
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
            currentTemperature *= (1 - coolingRate);
        }
        return bestSolution;

    }

    private double acceptProbability(double currentEnergy, double newEnergy, double temperature) {
        if (newEnergy < currentEnergy)
            return 1.1;
        return Math.exp(-(newEnergy - currentEnergy) / currentTemperature);

    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        SimulatedAnnealing sa = new SimulatedAnnealing();
        sa.initSolution();
        Solution best = sa.anneal();
        System.out.println(best);
        System.out.println("最短时间："+best.getConsumeTime());
    }
}
