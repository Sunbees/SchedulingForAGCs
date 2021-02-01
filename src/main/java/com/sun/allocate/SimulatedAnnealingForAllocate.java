package com.sun.allocate;

import com.sun.algorithm.SimulatedAnnealing;
import com.sun.collision_dec.Collision;
import com.sun.data.Data;
import com.sun.pojo.Solution;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SimulatedAnnealingForAllocate {
    // 初始温度
    private double currentTemperature = 1000;
    // 最低温度
    private final double minTemperature = 1;
    // 某一温度下迭代次数
    private final int internalLoop = 20;
    // 冷却比率
    private final double coolingRate = 0.4;
    // 初始解
    private Solution currentSolution;

    private List<Integer> currentAllocateNo;


    public Solution anneal(List<Integer> countForEveryType) throws IOException, CloneNotSupportedException {
        currentTemperature = 1000;
        Solution newSolution = null;

        currentAllocateNo = genIndividual(countForEveryType, TestData.typeStockMap);
        List<Integer> newAllocateNo;
        List<Integer> bestAllocateNo;
        Solution bestSolution = null;

        SimulatedAnnealing sa = new SimulatedAnnealing();
        TestData testData = new TestData();
        testData.initSolution(currentAllocateNo.size());
        sa.initSolution();
        currentSolution = sa.anneal(currentAllocateNo);
        double currentEnergy = currentSolution.getConsumeTime();
        testData.clearTaskMap();


        while (currentTemperature > minTemperature) {
            for (int i = 0; i < internalLoop; i++) {
                testData.initSolution(currentAllocateNo.size());
                sa.initSolution();
                newAllocateNo = generateNext(currentAllocateNo, countForEveryType, TestData.typeStockMap);
                newSolution = sa.anneal(newAllocateNo);
                double newEnergy = newSolution.getConsumeTime();


                if (acceptProbability(currentEnergy, newEnergy) > Math.random()) {
                    currentEnergy = newEnergy;
                    currentSolution = newSolution.clone();
                    currentAllocateNo = newAllocateNo;
                    if (bestSolution == null || currentSolution.getConsumeTime() < bestSolution.getConsumeTime()) {
                        bestSolution = currentSolution.clone();
                        bestAllocateNo = bestSolution.getAllocationNo();
                    }
                }
                testData.clearTaskMap();


            }
            currentTemperature *= (1 - coolingRate);
        }
        //bestSolution.getConsumeTime();
        return bestSolution;

    }

    private double acceptProbability(double currentEnergy, double newEnergy) {
        if (newEnergy < currentEnergy)
            return 1.1;
        return Math.exp(-(newEnergy - currentEnergy) / currentTemperature);

    }

    public List<Integer> genIndividual(List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        List<Integer> res = new ArrayList<>();
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
            res.addAll(selectedList);
        }
        return res;
    }

    public List<Integer> generateNext(List<Integer> preCode, List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        List<Integer> res = new ArrayList<>(preCode);
        int begin = 0;
        for (int i = 0; i < 3; i++) {
            int count = countForEveryType.get(i);
            List<Integer> availableList = availableStock.get(i);
            List<Integer> selectedList = preCode.subList(begin, begin + count);
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
            res.set(begin + index2, unSelectedList.get(index1));
            begin += count;
        }

        return res;
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        //SolutionForAllocate solution = new SolutionForAllocate();
        List<Integer> countForEveryType = new ArrayList<>();
        countForEveryType.add(2);
        countForEveryType.add(3);
        countForEveryType.add(4);
        SimulatedAnnealingForAllocate solution = new SimulatedAnnealingForAllocate();
        Solution anneal = solution.anneal(countForEveryType);
        //TestData.stockLocationMap.values().forEach(System.out::println);
        System.out.println(anneal);

        SimulatedAnnealingForAllocate solution2 = new SimulatedAnnealingForAllocate();
        Solution anneal2 = solution2.anneal(countForEveryType);
        System.out.println(anneal2);
        //List<Integer> prelist = solution.genIndividual(countForEveryType, TestData.typeStockMap);
        //prelist.forEach(e -> System.out.print(e + "\t"));
        //System.out.println();
        //
        //SimulatedAnnealing sa = new SimulatedAnnealing();
        //TestData testData = new TestData();
        //testData.initSolution(9);
        //sa.initSolution();
        //Solution best = sa.anneal(prelist);
        //System.out.println(best);
        //System.out.println("最短时间：" + best.getConsumeTime());
        //
        //
        //List<Integer> newList = solution.generateNext(prelist, countForEveryType, TestData.typeStockMap);
        //newList.forEach(e -> System.out.print(e + "\t"));
        //System.out.println();
        //
        //testData.initSolution(9);
        //sa.initSolution();
        //testData.clearTaskMap();
        //best = sa.anneal(newList);
        //System.out.println(best);
        //System.out.println("最短时间：" + best.getConsumeTime());
        //
        //prelist = newList;
        //
        //newList = solution.generateNext(prelist, countForEveryType, TestData.typeStockMap);
        //newList.forEach(e -> System.out.print(e + "\t"));
        //System.out.println();
        //
        //testData.initSolution(9);
        //sa.initSolution();
        //testData.clearTaskMap();
        //best = sa.anneal(newList);
        //System.out.println(best);
        //System.out.println("最短时间：" + best.getConsumeTime());

    }
}
