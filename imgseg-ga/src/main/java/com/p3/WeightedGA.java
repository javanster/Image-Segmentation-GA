package com.p3;

import java.util.List;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class WeightedGA {
    
    /**
     * This is the Weighted Genetic Algorithm constructor.
     * 
     */
    public WeightedGA() {

    }

    private int populationSize = Parameters.POPULATION_SIZE;
    private int lambda = populationSize * 3;

    public void run() {
        System.out.println("Running Weighted GA");
        Population population = new Population();
        System.out.println("Population generated");
        System.out.println();

        // sort individuals on individual.getWeightedFitness()
        List<Individual> firstIndividuals = population.getIndividuals();
        firstIndividuals.sort((individual1, individual2) -> {
            return individual2.getWeightedFitness().compareTo(individual1.getWeightedFitness());
        });

        for (int i = 0; i < Parameters.GENERATIONS; i++) {
            List<Individual> individuals = population.getIndividuals();
            printStats(population, i);
            
            // reset values for all individuals
            for (Individual individual : individuals) {
                individual.resetObjectiveValues();
            }

            for (int j = 0; j < (int) (lambda / 2); j++) {
                // pick two random unique numbers [0, populationSize) as parents
                int parent1Index = (int) (Math.random() * populationSize);
                int parent2Index = (int) (Math.random() * populationSize);
                while (parent1Index == parent2Index) {
                    parent2Index = (int) (Math.random() * populationSize);
                }
                // get the parents
                Individual parent1 = population.getIndividuals().get(parent1Index);
                Individual parent2 = population.getIndividuals().get(parent2Index);

                // crossover
                Individual[] children = Parameters.CROSSOVER_HANDLER.cross(parent1, parent2);

                // mutation
                Parameters.MUTATION_HANDLER.mutate(children[0]);
                Parameters.MUTATION_HANDLER.mutate(children[1]);

                // add the children to the populationu
                individuals.add(children[0]);
                individuals.add(children[1]);
            }

            // sort individuals on individual.getWeightedFitness()
            individuals.sort((individual1, individual2) -> {
                return individual2.getWeightedFitness().compareTo(individual1.getWeightedFitness());
            });

            // the new population should be the best populationSize individuals
            List<Individual> newPopulation = new ArrayList<>();
            for (int j = 0; j < populationSize; j++) {
                newPopulation.add(individuals.get(j));
            }

            // update the population
            population = new Population(newPopulation);         
        }
        writeResults(population);
        printFinalResults(population);
    }

    /**
     * Writes the phenotype of the individuals in the first pareto front of the
     * final population to files, i.e. the segmented images.
     * 
     * @param population the final population.
     */
    private static void writeResults(Population population) {
        List<Individual> currentBestIndividuals = new ArrayList<>();
        int lenNewPopulation = population.getIndividuals().size() >= 5 ? 5 : population.getIndividuals().size();

        for (int i = 0; i < lenNewPopulation; i++) {
            currentBestIndividuals.add(population.getIndividuals().get(i));
        }
        
        // Delete previous results of type 1
        File type1Dir = new File("results_WGA/" + Parameters.IMAGE_NAME + "/type_1/");
        File[] type1Files = type1Dir != null ? type1Dir.listFiles() : null;
        if (type1Files != null) {
            for (File file : type1Files) {
                file.delete();
            }
        }

        // Delete previous results of type 2
        File type2Dir = new File("results_WGA/" + Parameters.IMAGE_NAME + "/type_2/");
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
            ImageReaderWriter.writeImageWithSegments("results_WGA/" + Parameters.IMAGE_NAME + "/type_1/" + i + ".jpg", ind, false);
            ImageReaderWriter.writeImageWithSegments("results_WGA/" + Parameters.IMAGE_NAME + "/type_2/" + i + ".jpg", ind, true);
        }
    }

    /**
     * Print statistics for the current generation. Prints the average edge value,
     * connectivity measure, overall deviation and number of segments for the five
     * best rated individuals.
     * 
     * @param population the current population.
     * @param generation the current generation.
     */
    private static void printStats(Population population, int generation) {
        List<Individual> currentBestIndividuals = new ArrayList<>();
        int lenNewPopulation = population.getIndividuals().size() >= 5 ? 5 : population.getIndividuals().size();

        for (int i = 0; i < lenNewPopulation; i++) {
            currentBestIndividuals.add(population.getIndividuals().get(i));
        }

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
     * and overall deviation of up to five individuals in the population.
     * 
     * @param population the final population.
     */
    private static void printFinalResults(Population population) {
        List<Individual> currentBestIndividuals = new ArrayList<>();
        int lenNewPopulation = population.getIndividuals().size() >= 5 ? 5 : population.getIndividuals().size();
        for (int i = 0; i < lenNewPopulation; i++) {
            currentBestIndividuals.add(population.getIndividuals().get(i));
        }
        DecimalFormat df = new DecimalFormat("#.###");
        for (int i = 1; i <= lenNewPopulation; i++) {
            Individual individual = currentBestIndividuals.get(i - 1);
            System.out.println("Solution " + i + " - Edge value: " + df.format(ObjectiveFunctions.edgeValue(individual)) + ", Connectivity measure: " + df.format(ObjectiveFunctions.connectivityMeasure(individual)) + ", Overall deviation: " + df.format(ObjectiveFunctions.overallDeviation(individual)));
        }
    }

    public static void main(String[] args) {
        Parameters.IMAGE_NAME = "353013";
        Parameters.IMAGE = new Image("training_images/" + Parameters.IMAGE_NAME + "/Test image.jpg");
        Parameters.SEGMENTS_LOWEBOUND = 3;
        Parameters.SEGMENTS_UPPERBOUND = 8;
        Parameters.POPULATION_SIZE = 50;
        Parameters.PARENT_SELECTOR = new TournamentParentSelector();
        Parameters.TOURNAMENT_SIZE = 7;
        Parameters.IS_TOURNAMENT_REPLACEMENT_ALLOWED = false;
        Parameters.GENERATIONS = 20;
        Parameters.CROSSOVER_HANDLER = new OnePointCrosser();
        Parameters.MUTATION_PROBABILITY = 0.9;
        Parameters.MUTATION_STEP_SIZE = 7;
        Parameters.MUTATION_HANDLER = new StudassMutator();

        /* 
         * Edge value and overalldeviation improves with an increasing 
         * number of segments (reduced distance) whilst the opposite 
         * is the case for connectivity (avoids splitting segments 
         * where neighboring pixels would be in different segments).
         */
        Parameters.EDGE_WEIGHT = 1;
        Parameters.CONNECTIVITY_WEIGHT = 1000;
        Parameters.DEVIATION_WEIGHT = 2;

        WeightedGA WGA = new WeightedGA();
        WGA.run();
    }
}
