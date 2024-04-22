package com.p3;

public class OffspringGenerator {
    
    public static Individual[] generateOffspring(Individual parent1, Individual parent2) {

        Individual[] offspring = Parameters.CROSSOVER_HANDLER.cross(parent1, parent2);
        Parameters.MUTATION_HANDLER.mutate(offspring[0]);
        Parameters.MUTATION_HANDLER.mutate(offspring[1]);

        return offspring;
    }
}
