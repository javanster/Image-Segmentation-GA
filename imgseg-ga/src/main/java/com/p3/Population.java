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
    public Population() {
        List<Individual> individuals = new ArrayList<>();

        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            // pick a random number of segments between lowerBound and upperBound
            int numSegments = Parameters.SEGMENTS_LOWEBOUND + (int) (Math.random() * (Parameters.SEGMENTS_UPPERBOUND - Parameters.SEGMENTS_LOWEBOUND));
            System.out.println("Creating individual " + (i + 1) + " of " + Parameters.POPULATION_SIZE + ", with " + numSegments + " segments");
            individuals.add(new Individual(numSegments));
        }

        this.individuals = individuals;
    }


    public Population(List<Individual> individuals) {
        this.individuals = individuals;
    }

    public Population(Population population1, Population population2) {
        List<Individual> individualsFromBoth = population1.getIndividuals();
        individualsFromBoth.addAll(population2.getIndividuals());
        this.individuals = individualsFromBoth;
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
        Parameters.POPULATION_SIZE = 2;
        Parameters.IMAGE = new Image(imagePath);
        Parameters.SEGMENTS_LOWEBOUND = 5;
        Parameters.SEGMENTS_UPPERBOUND = 10;
        Population population = new Population();
        System.out.println(population.getIndividuals().size());
    }
}
