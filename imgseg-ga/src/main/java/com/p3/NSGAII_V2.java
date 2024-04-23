package com.p3;

import java.text.DecimalFormat;
import java.util.List;


public class NSGAII_V2 {

    public static void run() {
        Population population = new Population();
        System.out.println("Initial population generated");

        for (int gen = 1; gen <= Parameters.GENERATIONS; gen++) {
            Population parents = Parameters.PARENT_SELECTOR.selectParents(population);
            Population offspring = OffspringGenerator.generateOffspring(parents);
            population = new Population(parents, offspring);

            List<Individual> currentBestIndividuals = ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0);
            int numInd = currentBestIndividuals.size();
            double edgeValueValueSum = 0.0;
            double connectivityMeasureSum = 0.0;
            double overallDeviationSum = 0.0;
            double numSegmentsSum = 0;
            for (Individual individual : currentBestIndividuals) {
                edgeValueValueSum += ObjectiveFunctions.edgeValue(individual);
                connectivityMeasureSum += ObjectiveFunctions.connectivityMeasure(individual);
                overallDeviationSum += ObjectiveFunctions.overallDeviation(individual);
                numSegmentsSum += individual.getSegments().size();
            }
            DecimalFormat df = new DecimalFormat("#.###");
            System.out.println("Gen " + gen + " - Avg. EV: " + df.format(edgeValueValueSum / numInd) + " - Avg. CM: " + df.format(connectivityMeasureSum / numInd)
            + " - Avg. OD: " + df.format(overallDeviationSum / numInd) + " - Avg num segments: " + df.format(numSegmentsSum / numInd));
        }
        System.out.println("Num pareto fronts before reduction: " + ObjectiveFunctions.getParetoFronts(population.getIndividuals()).size());
        System.out.println("Size of first pareto front before reduction: " + ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0).size());
        population = Parameters.PARENT_SELECTOR.selectParents(population);
        List<Individual> currentBestIndividuals = ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0);
        System.out.println("Size of first pareto front after reduction: " + currentBestIndividuals.size());
        for (int i = 0; i < currentBestIndividuals.size(); i++) {
            Individual ind = currentBestIndividuals.get(i);
            ImageReader.writeImageWithSegments("test" + i + ".png", ind);
        }
        
    }

    public static void main(String[] args) {
        Parameters.IMAGE = new Image("training_images/118035/Test image.jpg");
        Parameters.SEGMENTS_LOWEBOUND = 12;
        Parameters.SEGMENTS_UPPERBOUND = 47;
        Parameters.POPULATION_SIZE = 30;
        Parameters.PARENT_SELECTOR = new TournamentParentSelector();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = false;
        Parameters.GENERATIONS = 40;
        Parameters.CROSSOVER_HANDLER = new OnePointCrosser();
        Parameters.MUTATION_PROBABILITY = 0.0000;
        Parameters.MUTATION_STEP_SIZE = 7;
        Parameters.MUTATION_HANDLER = new RandomResettingMutation();

        NSGAII_V2.run();
    }
    
}
