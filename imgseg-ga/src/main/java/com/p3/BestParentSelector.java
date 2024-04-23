package com.p3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.p3.interfaces.ParentSelector;

public class BestParentSelector implements ParentSelector {

    @Override
    public Population selectParents(Population population) {
        List<Individual> parents = new ArrayList<>();
        List<List<Individual>> paretoFronts = ObjectiveFunctions.getParetoFronts(population.getIndividuals());
        int paretoFront = 0;

        while (parents.size() < Parameters.POPULATION_SIZE && parents.size() + paretoFronts.get(paretoFront).size() <= Parameters.POPULATION_SIZE) {
            parents.addAll(paretoFronts.get(paretoFront));
            paretoFront++;
        }

        if (parents.size() < Parameters.POPULATION_SIZE) {
            int numRemainingParentsToAdd = Parameters.POPULATION_SIZE - parents.size();
            List<Individual> nextParetoFront = paretoFronts.get(paretoFront);
            Map<Individual, Double> crowdingDistances = ObjectiveFunctions.getCrowdingDistances(nextParetoFront);
            List<Individual> sortedFront = nextParetoFront.stream()
                .sorted(Comparator.comparingDouble(crowdingDistances::get).reversed())
                .collect(Collectors.toList());
            parents.addAll(sortedFront.subList(0, numRemainingParentsToAdd));
        }

        return new Population(parents);
    }

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Parameters.POPULATION_SIZE = 2;
        Parameters.IMAGE = new Image(imagePath);
        Parameters.SEGMENTS_LOWEBOUND = 5;
        Parameters.SEGMENTS_UPPERBOUND = 10;
        Population population = new Population();
        Parameters.POPULATION_SIZE = population.getIndividuals().size();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = true;
        ParentSelector parentSelector = new BestParentSelector();
        Population parents = parentSelector.selectParents(population);
        System.out.println(parents.getIndividuals().size());
    }
    
}
