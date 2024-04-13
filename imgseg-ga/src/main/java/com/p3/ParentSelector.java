package com.p3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;


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

        for (int i = 0; i < Parameters.PARENT_COUNT; i++) {
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

            List<Individual> rankedTournament = getRankedTournament(tournament);
            parents.add(rankedTournament.get(0));
        }

        return new Population(parents);
    }

    /**
     * Rank the individuals in a tournament based on Pareto front and crowding distance.
     * 
     * Based on:
     * Deb, K., Pratap, A., Agarwal, S., & Meyarivan, T. (2002). A fast and elitist multiobjective genetic algorithm:
     * NSGA-II. IEEE Transactions on Evolutionary Computation, 6(2), 185. https://doi.org/10.1109/4235.996017 
     * 
     * @param tournament The tournament to rank.
     * @return A list of individuals in the tournament, ranked by Pareto front and crowding distance.
     */
    private static List<Individual> getRankedTournament(List<Individual> tournament) {
        List<List<Individual>> paretoFronts = ObjectiveFunctions.getParetoFronts(tournament);
        Map<Individual, Double> crowdingDistances = ObjectiveFunctions.getCrowdingDistances(tournament);

        for (List<Individual> front : paretoFronts) {
            front.sort(Comparator.comparingDouble(crowdingDistances::get).reversed());
        }

        List<Individual> rankedTournament = new ArrayList<>();
        for (List<Individual> front : paretoFronts) {
            rankedTournament.addAll(front);
        }
        return rankedTournament;
    }

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Population population = new Population(10, imagePath);
        Parameters.PARENT_COUNT = population.getIndividuals().size();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = true;
        Population parents = ParentSelector.selectParents(population);
        System.out.println(parents.getIndividuals().size());
    }
}
