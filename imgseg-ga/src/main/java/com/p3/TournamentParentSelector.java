package com.p3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.p3.interfaces.ParentSelector;


public class TournamentParentSelector implements ParentSelector{

    /**
     * Selects parents for the next generation using a crowding tournament selection.
     * 
     * Based on:
     * 
     * Eiben & Smith, 2015, p. 84-86.
     * 
     * Ripon, K. S. N., Ali, L. E., Newaz, S., & Ma, J. (2017, November 28). A multi-objective evolutionary
     * algorithm for color image segmentation. SpringerLink.
     * https://link.springer.com/chapter/10.1007/978-3-319-71928-3_17 
     * 
     * @param population The population from which to select parents.
     * @return A population of selected parents.
     */
    @Override
    public Population selectParents(Population population) {
        List<Individual> parents = new ArrayList<>();
        Random random = new Random();

        Map<Individual, Integer> paretoFrontsMap = ObjectiveFunctions.getParetoFrontsMap(population.getIndividuals());

        while (parents.size() < Parameters.POPULATION_SIZE) {

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

            Individual bestIndividual = tournament.get(0);
            int bestParetoRank = paretoFrontsMap.get(bestIndividual);
            List<Individual> bestIndividuals = new ArrayList<>();
            
            for (Individual individual : tournament) {
                int individualParetoRank = paretoFrontsMap.get(individual);
                
                if (individualParetoRank < bestParetoRank) {
                    bestParetoRank = individualParetoRank;
                    bestIndividual = individual;
                    bestIndividuals.clear();
                    bestIndividuals.add(individual);
                } else if (individualParetoRank == bestParetoRank) {
                    bestIndividuals.add(individual);
                }
            }

            if (bestIndividuals.size() == 1) {
                parents.add(bestIndividual);
            } else {
                List<Individual> allIndividualsInParetoFront = new ArrayList<>();
                for (Individual individual : population.getIndividuals()) {
                    if (paretoFrontsMap.get(individual) == bestParetoRank) {
                        allIndividualsInParetoFront.add(individual);
                    }
                }
                Map<Individual, Double> crowdingDistances = ObjectiveFunctions.getCrowdingDistances(allIndividualsInParetoFront);
                Individual bestIndividualInTournament = bestIndividuals.get(0);
                double bestCrowdingDistance = crowdingDistances.get(bestIndividualInTournament);
                for (Individual individual : bestIndividuals) {
                    double crowdingDistance = crowdingDistances.get(individual);
                    if (crowdingDistance > bestCrowdingDistance) {
                        bestIndividualInTournament = individual;
                        bestCrowdingDistance = crowdingDistance;
                    }
                }

                parents.add(bestIndividualInTournament);
            }
        }

        return new Population(parents);
    }

    

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String imagePath = "training_images/118035/Test image.jpg";
        Parameters.POPULATION_SIZE = 2;
        Parameters.IMAGE = new Image(imagePath);
        Parameters.SEGMENTS_LOWEBOUND = 5;
        Parameters.SEGMENTS_UPPERBOUND = 10;
        Population population = new Population();
        Parameters.POPULATION_SIZE = population.getIndividuals().size();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = true;
        ParentSelector parentSelector = new TournamentParentSelector();
        Population parents = parentSelector.selectParents(population);
        System.out.println(parents.getIndividuals().size());
    }
}
