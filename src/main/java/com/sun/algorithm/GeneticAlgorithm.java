package com.sun.algorithm;

import com.sun.data.Data;
import com.sun.pojo.Population;
import com.sun.pojo.Solution;
import org.omg.CORBA.MARSHAL;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

@Service
public class GeneticAlgorithm {
    static public final int SPECIES_NUM = 20; // 种群数
    static public final int EVOLVE_NUM = 100; // 进化代数
    static public final float pc = 0.6f; // 交叉概率
    static public final float pm = 0.2f; // 变异概率

    // 开始遗传
    public Solution run(Population population) throws IOException, CloneNotSupportedException {
        createBeginningSpecies(population);
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

    private void createBeginningSpecies(Population population) {
        for (int i = 0; i < population.getSpeciesNum(); i++) {
            Solution solution = new Solution();
            solution.createRandomly(Data.solution.getPriority());
            population.add(solution);
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
        int lastNum = SPECIES_NUM-talentNum;
        for (int i = 0; i < lastNum; i++) {
            double p = Math.random();
            boolean flag = false;
            for (Solution solution : population.getPopulation()) {
                if(p<solution.getRate()&&solution!=bestSolution){
                    flag = true;
                    newPopulation.add(solution.clone());
                    break;
                }
            }
            if(!flag){
                newPopulation.add(population.getPopulation().get(SPECIES_NUM - 1).clone());
            }
        }
        Collections.shuffle(newPopulation.getPopulation());
        population.setPopulation(newPopulation.getPopulation());
    }

    private void calRate(Population population) throws IOException, CloneNotSupportedException {
        double totalFitness = 0;
        for (Solution solution : population.getPopulation()) {
            totalFitness += 1.0/solution.getConsumeTime();
        }
        double tmp = 0.0;
        for (Solution solution : population.getPopulation()) {
            tmp += (1.0/solution.getConsumeTime())/totalFitness;
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

    private Solution getBest(Population population) throws IOException, CloneNotSupportedException {
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

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        Population population = new Population(SPECIES_NUM);
        Solution bestSolution = ga.run(population);
        System.out.println(bestSolution);
        System.out.println(bestSolution.getConsumeTime());
    }
}
