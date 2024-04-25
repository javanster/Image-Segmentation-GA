package com.p3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

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
            getNonDominatedIndividuals(population).size());
        population = SurvivorSelector.selectSurvivors(population);
        System.out.println("Size of first pareto front after population reduction: " +
            getNonDominatedIndividuals(population).size());

        population = reduceToUniqueIndividuals(population);
        writeResults(population);
        printFinalResults(population);
    }

    /**
     * Get the non-dominated individuals in the first pareto front of the population.
     * 
     * @param population the population.
     * @return a list of non-dominated individuals.
     */
    private static List<Individual> getNonDominatedIndividuals(Population population) {
        return ObjectiveFunctions.getParetoFronts(population.getIndividuals()).get(0);
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
        List<Individual> currentBestIndividuals = getNonDominatedIndividuals(population);
        
        int numInd = currentBestIndividuals.size();
        double edgeValueValueSum = 0.0;
        double connectivityMeasureSum = 0.0;
        double overallDeviationSum = 0.0;
        double numSegmentsSum = 0;

        for (Individual individual : currentBestIndividuals) {
            edgeValueValueSum += individual.getEdgeValue();
            connectivityMeasureSum += individual.getConnectivityMeasure();
            overallDeviationSum += individual.getOverallDeviation();
            numSegmentsSum += individual.getSegments().size();
        }

        DecimalFormat df = new DecimalFormat("#.###");
        System.out.println("Gen " + generation + " - Avg. best EV: " + df.format(edgeValueValueSum / numInd) + " - Avg. best CM: " + df.format(connectivityMeasureSum / numInd)
        + " - Avg. best OD: " + df.format(overallDeviationSum / numInd) + " - Avg. best num segments: " + df.format(numSegmentsSum / numInd));
    }

    /**
     * Print the final results of the algorithm, i.e. the edge value, connectivity measure
     * and overall deviation of up to five individuals in the first pareto front of the
     * final population.
     * 
     * @param population the final population.
     */
    private static void printFinalResults(Population population) {
        List<Individual> currentBestIndividuals = getNonDominatedIndividuals(population);
        int indCountToPrint = currentBestIndividuals.size() > 5 ? 5 : currentBestIndividuals.size();
        DecimalFormat df = new DecimalFormat("#.###");
        for (int i = 1; i <= indCountToPrint; i++) {
            Individual individual = currentBestIndividuals.get(i - 1);
            System.out.println("Solution " + i + " - Edge value: " + df.format(ObjectiveFunctions.edgeValue(individual)) + ", Connectivity measure: " + df.format(ObjectiveFunctions.connectivityMeasure(individual)) + ", Overall deviation: " + df.format(ObjectiveFunctions.overallDeviation(individual)));
        }
    }

    /**
     * Writes the phenotype of the individuals in the first pareto front of the
     * final population to files, i.e. the segmented images.
     * 
     * @param population the final population.
     */
    private static void writeResults(Population population) {
        List<Individual> currentBestIndividuals = getNonDominatedIndividuals(population);
        
        // Delete previous results of type 1
        File type1Dir = new File("results_MOEA/" + Parameters.IMAGE_NAME + "/type_1/");
        File[] type1Files = type1Dir != null ? type1Dir.listFiles() : null;
        if (type1Files != null) {
            for (File file : type1Files) {
                file.delete();
            }
        }

        // Delete previous results of type 2
        File type2Dir = new File("results_MOEA/" + Parameters.IMAGE_NAME + "/type_2/");
        File[] type2Files = type2Dir != null ? type2Dir.listFiles() : null;
        if (type2Files != null) {
            for (File file : type2Files) {
                file.delete();
            }
        }

        System.out.println("Writing results of segmentations of image " + Parameters.IMAGE_NAME + " to files...");
        for (int i = 0; i < currentBestIndividuals.size(); i++) {

            // Make sure directories exist
            if (!type1Dir.exists()) {
                type1Dir.mkdirs();
            }
            if (!type2Dir.exists()) {
                type2Dir.mkdirs();
            }

            Individual ind = currentBestIndividuals.get(i);
            ImageReaderWriter.writeImageWithSegments("results_MOEA/" + Parameters.IMAGE_NAME + "/type_1/" + i + ".jpg", ind, false);
            ImageReaderWriter.writeImageWithSegments("results_MOEA/" + Parameters.IMAGE_NAME + "/type_2/" + i + ".jpg", ind, true);
        }
    }

    /**
     * Reduces the population to unique individuals, i.e. individuals with unique chromosomes.
     * 
     * @param population the population to reduce.
     * @return a new population with unique individuals.
     */
    public static Population reduceToUniqueIndividuals(Population population) {
        List<List<Integer>> uniqueChromosomes = new ArrayList<>();
        for (Individual individual : population.getIndividuals()) {
            if (!uniqueChromosomes.contains(individual.getChromosome())) {
                uniqueChromosomes.add(individual.getChromosome());
            }
        }
        List<Individual> uniqueIndividuals = new ArrayList<>();
        for (List<Integer> chromosome : uniqueChromosomes) {
            uniqueIndividuals.add(new Individual(chromosome));
        }
        System.out.println("Reduced population to " + uniqueIndividuals.size() + " unique individuals");
        return new Population(uniqueIndividuals);
    }

    public static void main(String[] args) {
        Parameters.IMAGE_NAME = "353013";
        Parameters.IMAGE = new Image("training_images/" + Parameters.IMAGE_NAME + "/Test image.jpg");
        Parameters.SEGMENTS_LOWEBOUND = 6;
        Parameters.SEGMENTS_UPPERBOUND = 15;
        Parameters.POPULATION_SIZE = 100;
        Parameters.PARENT_SELECTOR = new TournamentParentSelector();
        Parameters.TOURNAMENT_SIZE = 7;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = false;
        Parameters.GENERATIONS = 50;
        Parameters.CROSSOVER_HANDLER = new OnePointCrosser();
        Parameters.MUTATION_PROBABILITY = 0.9;
        Parameters.MUTATION_STEP_SIZE = 7;
        Parameters.MUTATION_HANDLER = new StudassMutator();

        NSGAII_V2.runGA();
    }
    
}
