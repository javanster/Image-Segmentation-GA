package com.p3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        List<Individual> unionList = new ArrayList<>(oldGeneration.getIndividuals());
        unionList.addAll(newGeneration.getIndividuals());
        Population union = new Population(unionList);

        List<List<Individual>> paretoFronts = ObjectiveFunctions.getParetoFronts(union.getIndividuals());
        int paretoFront = 0;

        while (survivors.size() < Parameters.POPULATION_SIZE && survivors.size() + paretoFronts.get(paretoFront).size() <= Parameters.POPULATION_SIZE) {
            survivors.addAll(paretoFronts.get(paretoFront));
            paretoFront++;
        }

        if (survivors.size() < Parameters.POPULATION_SIZE) {
            int numRemainingParentsToAdd = Parameters.POPULATION_SIZE - survivors.size();
            List<Individual> nextParetoFront = paretoFronts.get(paretoFront);
            Map<Individual, Double> crowdingDistances = ObjectiveFunctions.getCrowdingDistances(nextParetoFront);
            List<Individual> sortedFront = nextParetoFront.stream()
                .sorted(Comparator.comparingDouble(crowdingDistances::get).reversed())
                .collect(Collectors.toList());
            survivors.addAll(sortedFront.subList(0, numRemainingParentsToAdd));
        }

        return new Population(survivors);
    }
}
