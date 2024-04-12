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

    public Population(int populationSize, String imageFilePath) {
        List<Individual> individuals = new ArrayList<>();
        Image image = new Image(imageFilePath);
        for (int i = 0; i < populationSize; i++) {
            individuals.add(new Individual(image));
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
        Population population = new Population(2, imagePath);
        System.out.println(population.getIndividuals().size());
    }
}
