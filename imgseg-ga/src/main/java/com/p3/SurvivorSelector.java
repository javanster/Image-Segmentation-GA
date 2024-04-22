package com.p3;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that selects survivors for the next generation. Uses the Mu + Lambda survivor selection strategy,
 * where lambda is the population size.
 * 
 * Based on:
 * Deb, K., Pratap, A., Agarwal, S., & Meyarivan, T. (2002). A fast and elitist multiobjective genetic algorithm:
 * NSGA-II. IEEE Transactions on Evolutionary Computation, 6(2), p. 185. https://doi.org/10.1109/4235.996017 
 */
public class SurvivorSelector {
    
    public static Population selectSurvivors(Population oldGeneration, Population newGeneration) {
        List<Individual> survivors = new ArrayList<>();

        List<Individual> union = new ArrayList<>(oldGeneration.getIndividuals());
        union.addAll(newGeneration.getIndividuals());

        List<Individual> unionRanked = ObjectiveFunctions.nonDominatedSort(union);

        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            survivors.add(unionRanked.remove(0));
        }

        return new Population(survivors);
    }
}
