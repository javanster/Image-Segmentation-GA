package com.p3;

import java.util.List;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class NSGAII {
    
    /**
     * This is the Non-Dominated Sorting Genetic Algorithm II (NSGA-II) constructor.
     * 
     * 
     */
    public NSGAII() {

    }

    private int populationSize = 6;
    private int lambda = populationSize * 3;
    private String imagePath = "training_images/118035/Test image.jpg";
    private OnePointCrosser crossover = new OnePointCrosser();
    // private TwoPointCrosser crossover = new TwoPointCrosser();
    private RandomResettingMutation mutation = new RandomResettingMutation();

    private int lowerBound = 14;
    private int upperBound = 25;

    public void run() {
        System.out.println("Running NSGA-II");
        Population population = new Population(populationSize, imagePath, lowerBound, upperBound);
        System.out.println("Population generated");

        Individual bestIndividual = population.getIndividuals().get(0);
        // ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageHeight(), bestIndividual.getImage().getImageLength(), "test.png", bestIndividual);
        // System.out.println(bestIndividual.getSegments());
        System.out.println("Starter individual:");
        System.out.println("Edge: " + ObjectiveFunctions.edgeValue(bestIndividual) + ", Connectivity: " + ObjectiveFunctions.connectivityMeasure(bestIndividual) + ", Deviation: " + ObjectiveFunctions.overallDeviation(bestIndividual));
        System.out.println("Number of segments in first individual: " + bestIndividual.getSegments().size());

        for (int k = 0; k < populationSize; k++) {
            ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageLength(), bestIndividual.getImage().getImageHeight(), "createdImages/starter-" + k + ".png", population.getIndividuals().get(k));
        }
        // ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageLength(), bestIndividual.getImage().getImageHeight(), "createdImages/starter.png", bestIndividual);

        for (int i = 0; i < 10; i++) {
            List<Individual> individuals = population.getIndividuals();

            int total = 0;
            // check if the chromosome of each individual in individuals are different
            for (int l = 0; l < individuals.size(); l++) {
                for (int k = l + 1; k < individuals.size(); k++) {
                    if (individuals.get(l).getChromosome().equals(individuals.get(k).getChromosome())) {
                        total++;
                        break;
                    }
                }
            }
            if (total > 0) {
                System.out.println("Equal chromosomes in gen " + i + ": " + total);
            }


            for (int j = 0; j < (int) (lambda / 2); j++) {
                System.out.println("Generation " + i + ", individuals " + (2*j + 1) + " and " + (2*j + 2) + " of " + lambda);
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

                List<Integer> chromosome1 = new ArrayList<>(children[0].getChromosome());

                // mutation
                mutation.mutate(children[0]);
                mutation.mutate(children[1]);

                if (chromosome1.equals(children[0].getChromosome())) {
                    System.out.println("Equal chromosomes after mutation in gen " + i);
                }

                // add the children to the populationu
                individuals.add(children[0]);
                individuals.add(children[1]);
            }

            System.out.println("Generation " + i + " done - evaluating population");

            // evaluate the population
            List<List<Individual>> fronts = ObjectiveFunctions.getParetoFronts(individuals);
            Map<Individual, Double> crowdingDistance = ObjectiveFunctions.getCrowdingDistances(individuals);

            System.out.println("Selecting best individuas of generation " + i);

            // pick the best populationSize individuals based first on the front they are in and then on the crowding distance
            List<Individual> newPopulation = new ArrayList<>();
            for (int j = 0; j < fronts.size(); j++) {
                List<Individual> front = fronts.get(j);
                if (newPopulation.size() + front.size() <= populationSize) {
                    front.sort((individual1, individual2) -> {
                        return crowdingDistance.get(individual2).compareTo(crowdingDistance.get(individual1));
                    });
                    newPopulation.addAll(front);
                } else {

                    // sort the front based on crowding distance
                    front.sort((individual1, individual2) -> {
                        return crowdingDistance.get(individual2).compareTo(crowdingDistance.get(individual1));
                    });

                    // add the best individuals from the front to the new population
                    int newPopulationSize = newPopulation.size();
                    for (int k = 0; k < populationSize - newPopulationSize; k++) {
                        newPopulation.add(front.get(k));
                    }

                    break;
                }
            }
            // update the population
            population = new Population(newPopulation);

            // print the best individual in the population
            bestIndividual = population.getIndividuals().get(0);
            System.out.print("Best individual in generation " + i + ": ");
            System.out.println("Edge: " + ObjectiveFunctions.edgeValue(bestIndividual) + ", Connectivity: " + ObjectiveFunctions.connectivityMeasure(bestIndividual) + ", Deviation: " + ObjectiveFunctions.overallDeviation(bestIndividual));
            System.out.println("Number of segments: " + bestIndividual.getSegments().size());

            for (int k = 0; k < populationSize; k++) {
                ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageLength(), bestIndividual.getImage().getImageHeight(), "createdImages/test" + i + "-" + k + ".png", population.getIndividuals().get(k));
            }
            // ImageReader.writeImageWithSegments(bestIndividual.getImage().getPixels(), bestIndividual.getImage().getImageLength(), bestIndividual.getImage().getImageHeight(), "createdImages/test" + i + ".png", bestIndividual);
        }
    }

    public static void main(String[] args) {
        NSGAII nsgaII = new NSGAII();
        nsgaII.run();
    }
}
