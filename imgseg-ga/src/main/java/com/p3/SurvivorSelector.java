package com.p3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for selecting survivors from a population containg individuals of both the previous and current generation.
 * 
 */
public class SurvivorSelector {
    
    /**
     * Select survivors from the previous and current generation. The survivors are selected based on the
     * non-dominated sorting and crowding distance.
     * 
     * @param prevAndNewGen the population containing individuals from the previous and current generation.
     * @return the population containing the survivors.
     */
    public static Population selectSurvivors(Population prevAndNewGen) {
        List<Individual> survivors = new ArrayList<>();

        List<List<Individual>> paretoFronts = ObjectiveFunctions.getParetoFronts(prevAndNewGen.getIndividuals());
        int paretoFront = 0;

        // Adds individuals from subsequent pareto fronts until the population size is reached
        // or the addition of a new front would exceed the population size
        while (survivors.size() < Parameters.POPULATION_SIZE && survivors.size() + paretoFronts.get(paretoFront).size() <= Parameters.POPULATION_SIZE) {
            survivors.addAll(paretoFronts.get(paretoFront));
            paretoFront++;
        }

        // If the population size is not reached, add the individuals from the next pareto front based on crowding distance
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
