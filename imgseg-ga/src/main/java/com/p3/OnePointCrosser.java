package com.p3;

import java.util.List;
import java.util.Random;

import com.p3.interfaces.CrossoverHandler;

/**
 * A class that performs one-point crossover on two individuals.
 * 
 * Based on:
 * Eiben & Smith, 2015, p. 53.
 *
 * @param parent1 The first parent.
 * @param parent2 The second parent.
 * @return An array of two children.
 */
public class OnePointCrosser implements CrossoverHandler{

    /**
     * Crosses two individuals to create two children, using one-point crossover.
     */
    @Override
    public Individual[] cross(Individual parent1, Individual parent2) {
        Random random = new Random();
        List<Integer> chromosome1 = parent1.getChromosome();
        List<Integer> chromosome2 = parent2.getChromosome();

        int crossoverPoint = random.nextInt(chromosome1.size() - 1);

        List<Integer> child1 = new java.util.ArrayList<>(chromosome1.subList(0, chromosome1.size()));
        List<Integer> child2 = new java.util.ArrayList<>(chromosome2.subList(0, chromosome1.size()));

        for (int i = 0; i < crossoverPoint; i++) {
            child1.set(i, chromosome1.get(i));
            child2.set(i, chromosome2.get(i));
        }

        for (int i = crossoverPoint; i < chromosome1.size(); i++) {
            child1.set(i, chromosome2.get(i));
            child2.set(i, chromosome1.get(i));
        }

        Individual[] children = new Individual[2];
        children[0] = new Individual(child1);
        children[1] = new Individual(child2);

        return children;
    }
    
}
