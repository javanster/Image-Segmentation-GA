package com.p3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class ParentSelector {

    /**
     * Selects parents for the next generation using a crowding tournament selection.
     * 
     * Based on:
     * Eiben & Smith, 2015, p. 84-86. 
     * 
     * @param population The population from which to select parents.
     * @return A population of selected parents.
     */
    public static Population selectParents(Population population) {
        List<Individual> parents = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            List<Individual> tournament = new ArrayList<>();
            for (int j = 0; j < Parameters.TOURNAMENT_SIZE; j++) {
                List<Individual> individuals = new ArrayList<>(population.getIndividuals());
                int index = random.nextInt(population.getIndividuals().size());
                if (Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED) {
                    // If replacement is allowed, add a random individual from the population (with replacement)
                    tournament.add(individuals.get(index));
                } else {
                    // If replacement is not allowed, remove the selected individual from the population
                    tournament.add(individuals.remove(index));
                }
            }

            List<Individual> rankedTournament = ObjectiveFunctions.nonDominatedSort(tournament);
            parents.add(rankedTournament.get(0));
        }

        return new Population(parents);
    }

    

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String imagePath = "training_images/118035/Test image.jpg";
        Population population = new Population(4, imagePath, 5, 10);
        Parameters.POPULATION_SIZE = population.getIndividuals().size();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = true;
        Population parents = ParentSelector.selectParents(population);
        System.out.println(parents.getIndividuals().size());
    }
}
