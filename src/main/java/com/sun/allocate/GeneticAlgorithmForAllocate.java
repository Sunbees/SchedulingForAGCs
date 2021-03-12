package com.sun.allocate;

import com.sun.pojo.Population;
import com.sun.schedule.GeneticAlgorithm;
import com.sun.schedule.SimulatedAnnealing;
import com.sun.solution.Solution;
import com.sun.util.Util;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

public class GeneticAlgorithmForAllocate {
    public int SPECIES_NUM; // 种群数
    public int EVOLVE_NUM = 20; // 进化代数
    public float pc = 0.5f; // 交叉概率
    public float pm = 0.3f; // 变异概率
    public List<Integer> countForEveryType;
    public SimulatedAnnealing sa;


    public GeneticAlgorithmForAllocate(int SPECIES_NUM, int EVOLVE_NUM, float pc, float pm, List<Integer> countForEveryType) {
        this.SPECIES_NUM = SPECIES_NUM;
        this.EVOLVE_NUM = EVOLVE_NUM;
        this.pc = pc;
        this.pm = pm;
        this.countForEveryType = countForEveryType;
        TestData.initSolution(countForEveryType.stream().reduce(0, Integer::sum));
        sa = new SimulatedAnnealing();
        sa.initSolution();

    }

    public GeneticAlgorithmForAllocate(List<Integer> countForEveryType) {
        this.countForEveryType = countForEveryType;
        TestData.initSolution(countForEveryType.stream().reduce(0, Integer::sum));
        sa = new SimulatedAnnealing();
        sa.initSolution();

    }

    // 开始遗传
    public Solution run(Population population) throws IOException, CloneNotSupportedException {
        this.SPECIES_NUM = population.getSpeciesNum();

        createBeginningSpecies(population, countForEveryType, TestData.typeStockMap);
        for (int i = 1; i <= EVOLVE_NUM; i++) {
            // 选择
            select(population);

            // 交叉
            crossover(population);

            // 变异
            mutate(population);
        }
        Solution best = getBest(population);
        TestData.clearTaskMap();
        return sa.anneal(best.getAllocationNo(), 10000, 0.1, 0.1, 200);
        //return best;
    }

    private List<Set<Integer>> genIndividual(List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) {
        List<Set<Integer>> res = new ArrayList<>();
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
            res.add(selectedList);
        }
        return res;
    }

    private void createBeginningSpecies(Population population, List<Integer> countForEveryType, Map<Integer, List<Integer>> availableStock) throws IOException {
        for (int i = 0; i < population.getSpeciesNum(); i++) {
            Solution solution = sa.anneal(genIndividual(countForEveryType, availableStock), 1000, 1, 0.3, 10);
            population.add(solution);
            TestData.clearTaskMap();
        }
    }

    private void select(Population population) throws IOException, CloneNotSupportedException {
        Solution bestSolution = getBest(population);
        Population newPopulation = new Population(SPECIES_NUM);
        int talentNum = SPECIES_NUM / 4;
        for (int i = 0; i < talentNum; i++) {
            newPopulation.add(bestSolution.clone());
        }
        calRate(population);
        int lastNum = SPECIES_NUM - talentNum;
        for (int i = 0; i < lastNum; i++) {
            double p = Math.random();
            boolean flag = false;
            for (Solution solution : population.getPopulation()) {
                if (p < solution.getRate() && solution != bestSolution) {
                    flag = true;
                    newPopulation.add(solution.clone());
                    break;
                }
            }
            if (!flag) {
                newPopulation.add(population.getPopulation().get(SPECIES_NUM - 1).clone());
            }
        }
        Collections.shuffle(newPopulation.getPopulation());
        population.setPopulation(newPopulation.getPopulation());
    }

    private void calRate(Population population) throws IOException, CloneNotSupportedException {
        double totalFitness = 0;
        double maxTime = 0;
        for (Solution solution : population.getPopulation()) {
            maxTime = Math.max(maxTime, solution.getConsumeTime());
        }
        maxTime += 1000;
        for (Solution solution : population.getPopulation()) {
            totalFitness += (maxTime - solution.getConsumeTime());
        }
        double tmp = 0.0;
        for (Solution solution : population.getPopulation()) {
            tmp += (maxTime - solution.getConsumeTime()) / totalFitness;
            solution.setRate(tmp);
        }


    }

    private void crossover(Population population) {
        for (int find = 0; find < population.getSpeciesNum()-1; find += 2 / pc) {
            if (Math.random() <= pc) {
                Solution solution1 = population.getPopulation().get(find);
                Solution solution2 = population.getPopulation().get(find + 1);
                Pair<List<Set<Integer>>, List<Set<Integer>>> children = crossover(solution1.getAllocationNo(), solution2.getAllocationNo());
                solution1.setAllocationNo(children.getKey());
                solution2.setAllocationNo(children.getValue());
            }
        }

    }

    private void mutate(Population population) throws IOException {
        for (int i = 0; i < population.getPopulation().size(); i++) {
            Solution solution = population.getPopulation().get(i);
            if (Math.random() < pm) {
                solution.setAllocationNo(mutate(solution.getAllocationNo()));
            }
            TestData.clearTaskMap();
            population.getPopulation().set(i, sa.anneal(solution.getAllocationNo(), 100, 1, 0.4, 10));
            //solution.setConsumeTime(0);
            //solution.getConsumeTime();
        }
    }

    private Solution getBest(Population population) throws IOException {
        Solution bestSolution = null;
        double consumeTime = Double.MAX_VALUE;
        for (Solution solution : population.getPopulation()) {
            if (solution.getConsumeTime() < consumeTime) {
                consumeTime = solution.getConsumeTime();
                bestSolution = solution;
            }
        }
        return bestSolution;
    }

    private Pair<List<Set<Integer>>, List<Set<Integer>>> crossover(List<Set<Integer>> p1, List<Set<Integer>> p2) {
        List<Set<Integer>> common = new ArrayList<>();
        for (int i = 0; i < countForEveryType.size(); i++) {
            Set<Integer> set = new HashSet<>();
            for (int e : p1.get(i)) {
                if (p2.get(i).contains(e)) {
                    set.add(e);
                }
            }
            common.add(set);
        }
        List<Set<Integer>> child1 = new ArrayList<>();
        List<Set<Integer>> child2 = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < countForEveryType.size(); i++) {
            int count = countForEveryType.get(i);
            Set<Integer> set1 = new HashSet<>(common.get(i));
            Set<Integer> set2 = new HashSet<>(common.get(i));
            List<Integer> availableStock = TestData.typeStockMap.get(i);
            int size = availableStock.size();
            while (set1.size() < count) {
                int index = random.nextInt(size);
                set1.add(availableStock.get(index));
            }
            while (set2.size() < count) {
                int index = random.nextInt(size);
                set2.add(availableStock.get(index));
            }
            child1.add(set1);
            child2.add(set2);
        }
        return new Pair<>(child1, child2);
    }

    private List<Set<Integer>> mutate(List<Set<Integer>> alloNo) {
        List<Set<Integer>> res = new ArrayList<>(alloNo);
        List<List<Integer>> unselected = new ArrayList<>();
        for (int i = 0; i < countForEveryType.size(); i++) {
            List<Integer> set = new ArrayList<>();
            for (Integer e : TestData.typeStockMap.get(i)) {
                if (!res.get(i).contains(e)) {
                    set.add(e);
                }
            }
            unselected.add(set);
        }
        for (int i = 0; i < res.size(); i++) {
            if (unselected.get(i).size() > 0 && Math.random() < 1 - pm) {
                Random random = new Random();
                List<Integer> list = new ArrayList<>(res.get(i));
                int index1 = random.nextInt(list.size());
                int index2 = random.nextInt(unselected.get(i).size());
                list.set(index1, unselected.get(i).get(index2));
                res.set(i, new HashSet<>(list));
            }
        }
        return res;
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        List<Integer> countForEveryType = Arrays.asList(2, 3, 4);
        GeneticAlgorithmForAllocate ga4A = new GeneticAlgorithmForAllocate(countForEveryType);
        Population population = new Population(40);
        Solution run = ga4A.run(population);
        System.out.println(run);
        int correct = 0;
        for (int i = 1; i < 50; i++) {
            run = ga4A.run(population);
            System.out.println(run);
            if (run.getConsumeTime() < 1457.0) {
                ++correct;
            }
        }
        System.out.println(correct);

        //List<Integer> p1 = Arrays.asList(1, 3, 7, 8, 9, 10, 11, 12, 14);
        //List<Integer> p2 = Arrays.asList(1, 4, 6, 8, 9, 10, 11, 12, 13);

        //List<Set<Integer>> p1 = Util.convertToSet(countForEveryType, Arrays.asList(0, 3, 7, 8, 9, 10, 11, 12, 14));
        //List<Set<Integer>> p2 = Util.convertToSet(countForEveryType, Arrays.asList(0, 4, 6, 8, 9, 10, 11, 12, 13));
        //
        //long startTime = System.currentTimeMillis();    //获取开始时间
        //
        ////Pair<List<Integer>, List<Integer>> ans = ga4A.crossover(p1, p2);
        //Pair<List<Set<Integer>>, List<Set<Integer>>> ans = ga4A.crossover(p1, p2);
        //
        //p1 = ga4A.mutate(p1);
        //p2 = ga4A.mutate(p2);
        //
        //
        //long endTime = System.currentTimeMillis();    //获取结束时间
        //System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        //System.out.println(p1);
        //System.out.println(p2);
        //System.out.println(ans.getKey());
        //System.out.println(ans.getValue());
        //
        //
        //int count1 = 0;
        //int count2 = 0;
        //int count3 = 0;
        //int count4 = 0;
        //for (int i = 0; i < 10000; i++) {
        //    ans = ga4A.crossover(p1, p2);
        //    if (ans.getKey().get(0).contains(1)) {
        //        ++count1;
        //    }
        //    if (ans.getKey().get(0).contains(2)) {
        //        ++count2;
        //    }
        //    if (ans.getKey().get(0).contains(3)) {
        //        ++count3;
        //    }
        //    if (ans.getKey().get(0).contains(4)) {
        //        ++count4;
        //    }
        //    //if (ans.getValue().get(0).contains(1)) {
        //    //    ++count1;
        //    //}
        //    //if (ans.getValue().get(0).contains(2)) {
        //    //    ++count2;
        //    //}
        //    //if (ans.getValue().get(0).contains(3)) {
        //    //    ++count3;
        //    //}
        //    //if (ans.getValue().get(0).contains(4)) {
        //    //    ++count4;
        //    //}
        //}
        //
        //System.out.println(count1 + " " + count2 + " " + count3 + " " + count4);
        //GeneticAlgorithm ga = new GeneticAlgorithm();
        //population = new Population(20);
        //Solution bestSolution = ga.run(population);
        //System.out.println(bestSolution);
        //System.out.println(bestSolution.getConsumeTime());

    }
}
