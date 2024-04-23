package com.p3;

import java.util.List;

import com.p3.interfaces.MutationHandler;
import java.util.Random;

public class StudassMutator implements MutationHandler {
    
    /**
     * Mutates an individual.
     * 
     * @param child The individual to mutate.
     * @return void
     */
    @Override
    public void mutate(Individual child) {
        List<Integer> chromosome = child.getChromosome();
        int length = chromosome.size();
        Random random = new Random();

        if (random.nextDouble() < Parameters.MUTATION_PROBABILITY) {
            int i = random.nextInt(length);

            // set to a random int in interval [0, 8]
            chromosome.set(i, random.nextInt(9));
            child.setChromosome(chromosome);
        }
    }
}

