package com.sun.allocate;

import com.sun.algorithm.GeneticAlgorithm;
import com.sun.algorithm.SimulatedAnnealing;
import com.sun.data.Data;
import com.sun.pojo.Population;
import com.sun.pojo.Solution;
import com.sun.util.Util;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

public class GeneticAlgorithmForAllocate {
    public int SPECIES_NUM = 20; // 种群数
    public int EVOLVE_NUM = 100; // 进化代数
    public float pc = 0.6f; // 交叉概率
    public float pm = 0.2f; // 变异概率
    public List<Integer> countForEveryType;


    public GeneticAlgorithmForAllocate(int SPECIES_NUM, int EVOLVE_NUM, float pc, float pm, List<Integer> countForEveryType) {
        this.SPECIES_NUM = SPECIES_NUM;
        this.EVOLVE_NUM = EVOLVE_NUM;
        this.pc = pc;
        this.pm = pm;
        this.countForEveryType = countForEveryType;
    }

    public GeneticAlgorithmForAllocate(List<Integer> countForEveryType) {
        this.countForEveryType = countForEveryType;
    }

    // 开始遗传
    public Solution run(Population population) throws IOException, CloneNotSupportedException {

        createBeginningSpecies(population, countForEveryType, TestData.typeStockMap);
        for (int i = 1; i <= EVOLVE_NUM; i++) {
            // 选择
            select(population);

            // 交叉
            crossover(population);

            // 变异
            mutate(population);
        }
        return getBest(population);
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
        TestData.initSolution(countForEveryType.stream().reduce(0, Integer::sum));
        SimulatedAnnealing sa = new SimulatedAnnealing();
        sa.initSolution();
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
        if (Math.random() <= pc) {
            Random random = new Random();
            int find = random.nextInt(population.getSpeciesNum() - 1);
            Solution solution1 = population.getPopulation().get(find);
            Solution solution2 = population.getPopulation().get(find + 1);
            int begin = random.nextInt(Data.TASK_NUM);
            for (int i = begin; i < Data.TASK_NUM; i++) {
                int first, second;
                for (first = 0; !solution1.getPriority(first).equals(solution2.getPriority(i)); first++) ;
                for (second = 0; !solution1.getPriority(i).equals(solution2.getPriority(second)); second++) ;

                Integer temp = solution1.getPriority(i);
                solution1.setPriority(i, solution2.getPriority(i));
                solution2.setPriority(i, temp);

                solution1.setPriority(first, solution2.getPriority(i));
                solution2.setPriority(second, solution1.getPriority(i));
                solution1.setConsumeTime(0);
                solution2.setConsumeTime(0);

            }
        }
    }

    private void mutate(Population population) {
        for (Solution solution : population.getPopulation()) {
            if (Math.random() < pm) {
                Random random = new Random();
                int left = random.nextInt(Data.TASK_NUM);
                int right = random.nextInt(Data.TASK_NUM);
                if (left > right) {
                    int tmp = left;
                    left = right;
                    right = tmp;
                }
                while (left < right) {
                    Integer temp = solution.getPriority(left);
                    solution.setPriority(left, solution.getPriority(right));
                    solution.setPriority(right, temp);
                    ++left;
                    --right;
                }
            }
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

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        List<Integer> countForEveryType = Arrays.asList(2, 3, 4);
        GeneticAlgorithmForAllocate ga4A = new GeneticAlgorithmForAllocate(countForEveryType);
        //Population population = new Population(20);
        //ga4A.run(population);

        //List<Integer> p1 = Arrays.asList(1, 3, 7, 8, 9, 10, 11, 12, 14);
        //List<Integer> p2 = Arrays.asList(1, 4, 6, 8, 9, 10, 11, 12, 13);

        List<Set<Integer>> p1 = Util.convertToSet(countForEveryType, Arrays.asList(1, 3, 7, 8, 9, 10, 11, 12, 14));
        List<Set<Integer>> p2 = Util.convertToSet(countForEveryType, Arrays.asList(1, 4, 6, 8, 9, 10, 11, 12, 13));

        long startTime = System.currentTimeMillis();    //获取开始时间

        //Pair<List<Integer>, List<Integer>> ans = ga4A.crossover(p1, p2);
        Pair<List<Set<Integer>>, List<Set<Integer>>> ans = ga4A.crossover(p1, p2);

        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        System.out.println(ans.getKey());
        System.out.println(ans.getValue());


        //GeneticAlgorithm ga = new GeneticAlgorithm();
        //Population population = new Population(20);
        //Solution bestSolution = ga.run(population);
        //System.out.println(bestSolution);
        //System.out.println(bestSolution.getConsumeTime());

    }
}
