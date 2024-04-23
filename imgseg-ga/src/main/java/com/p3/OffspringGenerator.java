package com.p3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that generates offspring from two parents by performing crossover and mutation.
 */
public class OffspringGenerator {
    
    public static Population generateOffspring(Population parents) {

        // Might remove shuffling later
        List<Individual> parentIndividuals = parents.getIndividuals();
        Collections.shuffle(parentIndividuals);

        List<Individual> offspringList = new ArrayList<>();
        for (int j = 0; j < parentIndividuals.size(); j += 2) {
            Individual parent1 = parentIndividuals.get(j);
            Individual parent2 = parentIndividuals.get(j + 1);
            Individual[] offspring = Parameters.CROSSOVER_HANDLER.cross(parent1, parent2);
            Parameters.MUTATION_HANDLER.mutate(offspring[0]);
            Parameters.MUTATION_HANDLER.mutate(offspring[1]);
            offspringList.add(offspring[0]);
            offspringList.add(offspring[1]);
        }

        return new Population(offspringList);
    }
}
