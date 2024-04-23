package com.p3;

import java.util.List;

import java.util.ArrayList;
import java.util.Map;


public class WeightedGA {
    
    /**
     * This is the Non-Dominated Sorting Genetic Algorithm II (NSGA-II) constructor.
     * 
     * 
     */
    public WeightedGA() {

    }

    private int populationSize = Parameters.POPULATION_SIZE;
    private int lambda = populationSize * 3;
    private OnePointCrosser crossover = new OnePointCrosser();
    // private TwoPointCrosser crossover = new TwoPointCrosser();
    private RandomResettingMutation mutation = new RandomResettingMutation();


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

        Individual bestIndividual = firstIndividuals.get(0);
        // System.out.println(bestIndividual.getSegments());
        System.out.println("Starter individual:");
        System.out.println("Edge: " + ObjectiveFunctions.edgeValue(bestIndividual) + ", Connectivity: " + ObjectiveFunctions.connectivityMeasure(bestIndividual) + ", Deviation: " + ObjectiveFunctions.overallDeviation(bestIndividual));
        System.out.println("Number of segments in first individual: " + bestIndividual.getSegments().size());
        System.out.println();

        for (int k = 0; k < (int) (populationSize / 10); k++) {
            ImageReaderWriter.writeImageWithSegments("createdImages/starter-" + k + ".png", population.getIndividuals().get(k), true);
        }
        // ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageLength(), bestIndividual.getImage().getImageHeight(), "createdImages/starter.png", bestIndividual);

        for (int i = 0; i < 10; i++) {
            List<Individual> individuals = population.getIndividuals();
            
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
                Individual[] children = crossover.cross(parent1, parent2);

                // mutation
                mutation.mutate(children[0]);
                mutation.mutate(children[1]);

                // add the children to the populationu
                individuals.add(children[0]);
                individuals.add(children[1]);
            }

            System.out.println("Generation " + i + " done - evaluating population");

            // evaluate the population

            // sort individuals on individual.getWeightedFitness()
            individuals.sort((individual1, individual2) -> {
                return individual2.getWeightedFitness().compareTo(individual1.getWeightedFitness());
            });

            System.out.println("Selecting best individuas of generation " + i);

            // the new population should be the best populationSize individuals
            List<Individual> newPopulation = new ArrayList<>();
            for (int j = 0; j < populationSize; j++) {
                newPopulation.add(individuals.get(j));
            }

            // update the population
            population = new Population(newPopulation);

            // print the best individual in the population
            bestIndividual = population.getIndividuals().get(0);
            System.out.print("Best individual in generation " + i + ": ");
            System.out.println("Edge: " + ObjectiveFunctions.edgeValue(bestIndividual) + ", Connectivity: " + ObjectiveFunctions.connectivityMeasure(bestIndividual) + ", Deviation: " + ObjectiveFunctions.overallDeviation(bestIndividual));
            System.out.println("Number of segments: " + bestIndividual.getSegments().size());
            System.out.println();
            
            // for (int k = 0; k < populationSize; k++) {
            //     ImageReader.writeImageWithSegments("createdImages/test" + i + "-" + k + ".png", population.getIndividuals().get(k), true);
            // }
            // ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageLength(), bestIndividual.getImage().getImageHeight(), "createdImages/test" + i + ".png", bestIndividual);
        }
        for (int k = 0; k < (int) (populationSize / 10); k++) {
            ImageReaderWriter.writeImageWithSegments("createdImages/testFinal-" + k + ".png", population.getIndividuals().get(k), true);
        }
    }

    public static void main(String[] args) {
        String imagePath = "training_images/86016/Test image.jpg";
        Parameters.POPULATION_SIZE = 50;
        Parameters.IMAGE = new Image(imagePath);
        Parameters.SEGMENTS_LOWEBOUND = 4;
        Parameters.SEGMENTS_UPPERBOUND = 41;
        Parameters.MUTATION_PROBABILITY = 0.0000001;

        Parameters.EDGE_WEIGHT = 1;
        Parameters.CONNECTIVITY_WEIGHT = 1000;
        Parameters.DEVIATION_WEIGHT = 2;

        WeightedGA WGA = new WeightedGA();
        WGA.run();
    }
}
