package com.p3;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a population of individuals.
 *
 * @param populationSize The size of the population.
 * @param imageFilePath The path to the image file.
 */
public class Population {
    
    private List<Individual> individuals;

    /**
     * Constructs a Population object with the specified parameters.
     *
     * @param populationSize the size of the population
     * @param imageFilePath the file path of the image
     * @param lowerBound the lower bound for the number of segments
     * @param upperBound the upper bound for the number of segments
     */
    public Population(int populationSize, String imageFilePath, int lowerBound, int upperBound) {
        Image image = new Image(imageFilePath);
        List<Individual> individuals = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            // pick a random number of segments between lowerBound and upperBound
            int numSegments = lowerBound + (int) (Math.random() * (upperBound - lowerBound));
            System.out.println("Creating individual " + (i + 1) + " of " + populationSize + ", with " + numSegments + " segments");
            individuals.add(new Individual(image, numSegments));
        }

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

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Population population = new Population(2, imagePath, 5, 10);
        System.out.println(population.getIndividuals().size());
    }
}
