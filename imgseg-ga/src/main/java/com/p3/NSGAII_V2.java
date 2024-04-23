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
        BestParentSelector bestParentSelector = new BestParentSelector();
        population = bestParentSelector.selectParents(population);
        List<Individual> currentBestIndividuals = ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0);
        System.out.println("Size of first pareto front after reduction: " + currentBestIndividuals.size());
        for (int i = 0; i < currentBestIndividuals.size(); i++) {
            Individual ind = currentBestIndividuals.get(i);
            ImageReader.writeImageWithSegments("evaluator/student_segments/result" + i + ".png", ind, true);
        }
        
    }

    public static void main(String[] args) {
        Parameters.IMAGE = new Image("training_images/86016/Test image.jpg");
        Parameters.SEGMENTS_LOWEBOUND = 4;
        Parameters.SEGMENTS_UPPERBOUND = 41;
        Parameters.POPULATION_SIZE = 100;
        Parameters.PARENT_SELECTOR = new TournamentParentSelector();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = false;
        Parameters.GENERATIONS = 1;
        Parameters.CROSSOVER_HANDLER = new OnePointCrosser();
        Parameters.MUTATION_PROBABILITY = 0.1;
        Parameters.MUTATION_STEP_SIZE = 7;
        Parameters.MUTATION_HANDLER = new StudassMutator();

        NSGAII_V2.run();
    }
    
}
