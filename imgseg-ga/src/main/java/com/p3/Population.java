package com.p3;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a population of individuals.
 */
public class Population {
    
    private List<Individual> individuals;

    /**
     * Creates a population of individuals. The number of individuals in the population is determined by the
     * Parameters.POPULATION_SIZE parameter. Individuals are created with a random number of segments between
     * Parameters.SEGMENTS_LOWEBOUND and Parameters.SEGMENTS_UPPERBOUND, with at least one individual for each
     * segment count.
     */
    public Population() {
        List<Individual> individuals = new ArrayList<>();


        for (int i = 0; i < Parameters.POPULATION_SIZE; i++) {
            int numSegments = i;
            
            // Ensures that every segment count has at least one individual
            if (numSegments >= Parameters.SEGMENTS_LOWEBOUND && numSegments <= Parameters.SEGMENTS_UPPERBOUND) {
                individuals.add(new Individual(i));
            } else {
                // pick a random number of segments between lowerBound and upperBound
                numSegments = Parameters.SEGMENTS_LOWEBOUND + (int) (Math.random() * (Parameters.SEGMENTS_UPPERBOUND - Parameters.SEGMENTS_LOWEBOUND));
                individuals.add(new Individual(numSegments));
            }
            System.out.println("Creating individual " + (i + 1) + " of " + Parameters.POPULATION_SIZE + ", with " + numSegments + " segments");
        }

        this.individuals = individuals;
    }


    /**
     * Creates a population of individuals from a list of individuals.
     * 
     * @param individuals A list of individuals.
     */
    public Population(List<Individual> individuals) {
        this.individuals = individuals;
    }

    /**
     * Creates a population of individuals from two populations.
     * 
     * @param population1 The first population.
     * @param population2 The second population.
     */
    public Population(Population population1, Population population2) {
        List<Individual> individualsFromBoth = population1.getIndividuals();
        individualsFromBoth.addAll(population2.getIndividuals());
        this.individuals = individualsFromBoth;
    }

    /**
     * Returns a list of copies of individuals in the population.
     * 
     * @return A list of copies of individuals in the population.
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
