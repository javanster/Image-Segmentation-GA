package com.p3;

/**
 * A class that generates offspring from two parents by performing crossover and mutation.
 */
public class OffspringGenerator {
    
    /**
     * Generates offspring from two parents by performing crossover and mutation. Crossover and mutation
     * is done according to the parameters set in the Parameters class.
     * 
     * @param parent1 The first parent.
     * @param parent2 The second parent.
     * @return An array of two individuals, which are the offspring of the two parents.
     */
    public static Individual[] generateOffspring(Individual parent1, Individual parent2) {

        Individual[] offspring = Parameters.CROSSOVER_HANDLER.cross(parent1, parent2);
        Parameters.MUTATION_HANDLER.mutate(offspring[0]);
        Parameters.MUTATION_HANDLER.mutate(offspring[1]);

        return offspring;
    }
}
