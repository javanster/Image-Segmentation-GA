package com.p3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Represents a population of individuals.
 *
 * @param populationSize The size of the population.
 * @param imageFilePath The path to the image file.
 */
public class Population {
    
    private List<Individual> individuals;

    // public Population(int populationSize, String imageFilePath) {
    //     List<Individual> individuals = new ArrayList<>();
    //     Image image = new Image(imageFilePath);
    //     for (int i = 0; i < populationSize; i++) {
    //         System.out.println("Creating individual " + i + 1 + " of " + populationSize);
    //         individuals.add(new Individual(image));
    //     }
    //     this.individuals = individuals;
    // }

    public Population(int populationSize, String imageFilePath, int lowerBound, int upperBound) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Individual>> futures = new ArrayList<>();
        Image image = new Image(imageFilePath);

        for (int i = 0; i < populationSize; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                // pick a random number of segments between lowerBound and upperBound
                int numSegments = lowerBound + (int) (Math.random() * (upperBound - lowerBound));
                System.out.println("Creating individual " + (index + 1) + " of " + populationSize + ", with " + numSegments + " segments");
                return new Individual(image, numSegments);
            }));
        }

        List<Individual> individuals = new ArrayList<>();
        for (Future<Individual> future : futures) {
            individuals.add(future.get());
        }

        executor.shutdown();
        this.individuals = individuals;
    }

    public Population(List<Individual> individuals) {
        this.individuals = individuals;
    }

    /**
     * Returns a list of individuals in the population.
     * 
     * @return A list of individuals in the population.
     */
    public List<Individual> getIndividuals() {
        return new ArrayList<>(this.individuals);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String imagePath = "training_images/118035/Test image.jpg";
        Population population = new Population(2, imagePath, 5, 10);
        System.out.println(population.getIndividuals().size());
    }
}
