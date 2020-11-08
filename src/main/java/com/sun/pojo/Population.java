package com.sun.pojo;

import java.util.ArrayList;
import java.util.List;

public class Population {
    private List<Solution> population;
    private int speciesNum;

    public List<Solution> getPopulation() {
        return population;
    }

    public void setPopulation(List<Solution> population) {
        this.population = population;
    }

    public int getSpeciesNum() {
        return speciesNum;
    }

    public void setSpeciesNum(int speciesNum) {
        this.speciesNum = speciesNum;
    }

    public Population(int speciesNum) {
        this.population = new ArrayList<>();
        this.speciesNum = speciesNum;
    }

    public void add(Solution solution) {
        this.population.add(solution);
    }
}
