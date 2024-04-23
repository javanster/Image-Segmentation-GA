package com.p3;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Class for running our second version of the NSGA-II algorithm.
 */
public class NSGAII_V2 {

    /**
     * Run the NSGA-II algorithm.
     * 
     * NB! The parameters in the Parameters class must be set before running this method.
     */
    public static void runGA() {
        Population population = new Population();
        System.out.println("Initial population generated");

        for (int gen = 1; gen <= Parameters.GENERATIONS; gen++) {

            printStats(population, gen);
            
            Population parents = Parameters.PARENT_SELECTOR.selectParents(population); 
            Population offspring = OffspringGenerator.generateOffspring(parents);
            population = new Population(parents, offspring);
            // Survivor selection not necessary until termination, since parent selection
            // reduces population to correct population size
        }

        System.out.println("Size of first pareto front before population reduction: " +
            ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0).size());
        population = SurvivorSelector.selectSurvivors(population);
        System.out.println("Size of first pareto front after population reduction: " +
            ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0).size());

        writeResults(population);
    }

    /**
     * Print statistics for the current generation. Prints the average edge value,
     * connectivity measure, overall deviation and number of segments for individuals
     * in the first pareto front of the population.
     * 
     * @param population the current population.
     * @param generation the current generation.
     */
    private static void printStats(Population population, int generation) {
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
        System.out.println("Gen " + generation + " - Avg. best EV: " + df.format(edgeValueValueSum / numInd) + " - Avg. best CM: " + df.format(connectivityMeasureSum / numInd)
        + " - Avg. best OD: " + df.format(overallDeviationSum / numInd) + " - Avg. best num segments: " + df.format(numSegmentsSum / numInd));
    }

    /**
     * Writes the phenotype of the individuals in the first pareto front of the
     * final population to files, i.e. the segmented images.
     * 
     * @param population the final population.
     */
    private static void writeResults(Population population) {
        List<Individual> currentBestIndividuals = ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0);
        
        for (int i = 0; i < currentBestIndividuals.size(); i++) {
            Individual ind = currentBestIndividuals.get(i);
            ImageReaderWriter.writeImageWithSegments("evaluator/student_segments/result" + i + ".png", ind, true);
        }
    }

    public static void main(String[] args) {
        Parameters.IMAGE = new Image("training_images/176035/Test image.jpg");
        Parameters.SEGMENTS_LOWEBOUND = 8;
        Parameters.SEGMENTS_UPPERBOUND = 39;
        Parameters.POPULATION_SIZE = 100;
        Parameters.PARENT_SELECTOR = new TournamentParentSelector();
        Parameters.TOURNAMENT_SIZE = 4;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = false;
        Parameters.GENERATIONS = 10;
        Parameters.CROSSOVER_HANDLER = new OnePointCrosser();
        Parameters.MUTATION_PROBABILITY = 0.2;
        Parameters.MUTATION_STEP_SIZE = 7;
        Parameters.MUTATION_HANDLER = new StudassMutator();

        NSGAII_V2.runGA();
    }
    
}
