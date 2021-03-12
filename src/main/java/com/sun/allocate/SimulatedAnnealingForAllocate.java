package com.sun.allocate;


import com.sun.schedule.SimulatedAnnealing;
import com.sun.solution.Solution;
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
    private final int internalLoop = 10;
    // 冷却比率
    private final double coolingRate = 0.3;
    // 初始解
    private Solution currentSolution;

    private List<Set<Integer>> currentAllocateNo;


    public Solution anneal(List<Integer> countForEveryType) throws IOException {
        currentTemperature = 1000;
        Solution newSolution = null;

        currentAllocateNo = genIndividual(countForEveryType, TestData.typeStockMap);
        List<Set<Integer>> newAllocateNo;
        List<Set<Integer>> bestAllocateNo = null;
        Solution bestSolution = null;

        SimulatedAnnealing sa = new SimulatedAnnealing();
        TestData.initSolution(countForEveryType.stream().reduce(0, Integer::sum));
        sa.initSolution();
        currentSolution = sa.anneal(currentAllocateNo, 1000, 1, 0.3, 10);
        double currentEnergy = currentSolution.getConsumeTime();
        TestData.clearTaskMap();


        while (currentTemperature > minTemperature) {
            for (int i = 0; i < internalLoop; i++) {
                TestData.initSolution(countForEveryType.stream().reduce(0, Integer::sum));
                sa.initSolution();
                newAllocateNo = generateNext(currentAllocateNo, countForEveryType, TestData.typeStockMap);
                newSolution = sa.anneal(newAllocateNo, 1000, 1, 0.2, 5);
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
                TestData.clearTaskMap();
            }
            currentTemperature *= (1 - coolingRate);
        }
        return sa.anneal(bestAllocateNo, 10000, 0.1, 0.1, 200);
        //bestSolution.getConsumeTime();
        //return bestSolution;

    }

    private double acceptProbability(double currentEnergy, double newEnergy) {
        if (newEnergy < currentEnergy)
            return 1.1;
        return Math.exp(-(newEnergy - currentEnergy) / currentTemperature);

    }

    public List<Set<Integer>> genIndividual(List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        List<Set<Integer>> res = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int count = countForEveryType.get(i);
            List<Integer> availableList = availableStock.get(i);
            Set<Integer> selectedSet = new TreeSet<>(Comparator.comparingInt(e -> e));
            Random random = new Random();
            int index;
            while (selectedSet.size() < count) {
                index = random.nextInt(availableList.size());
                selectedSet.add(availableList.get(index));
            }
            res.add(selectedSet);
        }
        return res;
    }

    public List<Set<Integer>> generateNext(List<Set<Integer>> preCode, List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        List<Set<Integer>> res = new ArrayList<>(preCode);
        for (int i = 0; i < 3; i++) {
            int count = countForEveryType.get(i);
            List<Integer> availableList = availableStock.get(i);
            List<Integer> selectedList = new ArrayList<>(preCode.get(i));
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
            int a = unSelectedList.get(index1);
            int b = selectedList.get(index2);
            res.get(i).remove(b);
            res.get(i).add(a);
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
        //List<Set<Integer>> sets = solution.genIndividual(countForEveryType, TestData.typeStockMap);
        //sets.forEach(set->{
        //    for(int s: set) {
        //        System.out.print(s+" ");
        //    }
        //    System.out.println();
        //});
        //
        //sets = solution.generateNext(sets, countForEveryType, TestData.typeStockMap);
        //sets.forEach(set->{
        //    for(int s: set) {
        //        System.out.print(s+" ");
        //    }
        //    System.out.println();
        //});

        //long startTime = System.currentTimeMillis();    //获取开始时间


        Solution anneal = solution.anneal(countForEveryType);

        //long endTime = System.currentTimeMillis();    //获取结束时间

        //System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        //TestData.stockLocationMap.values().forEach(System.out::println);
        System.out.println(anneal);
        int correctCount = 0;
        for (int i = 1; i < 50; i++) {
            anneal = solution.anneal(countForEveryType);
            System.out.println(anneal);
            if (anneal.getConsumeTime() < 1457.0) {
                ++correctCount;
            }
        }
        System.out.println(correctCount);

        SimulatedAnnealingForAllocate solution2 = new SimulatedAnnealingForAllocate();
        //startTime = System.currentTimeMillis();    //获取开始时间
        //Solution anneal2 = solution.anneal(countForEveryType);
        ////endTime = System.currentTimeMillis();    //获取结束时间
        ////System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        //System.out.println(anneal2);

    }
}
