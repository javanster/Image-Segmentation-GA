package com.p3;

import com.p3.interfaces.MutationHandler;

import java.util.List;
import java.util.Random;

public class CreepMutation implements MutationHandler {
    
    /**
     * Mutates an individual.
     * 
     * @param child The individual to mutate.
     * @return void
     */
    @Override
    public void mutate(Individual child) {
        Random random = new Random();
        List<Integer> chromosome = child.getChromosome();
        int length = chromosome.size();
        for (int i = 0; i < length; i++) {
            if (random.nextDouble() < Parameters.MUTATION_PROBABILITY) {
                int currentValue = chromosome.get(i);

                // Generate a random number from a triangular distribution
                double u = random.nextDouble();
                double tempMutationValue = (u < 0.5) ? Math.sqrt(u) : -Math.sqrt(1 - u);
                int mutationValue = (int) (tempMutationValue * Parameters.MUTATION_STEP_SIZE);

                int newValue = currentValue + mutationValue;

                // Ensure the new value is within the valid range [0, 8]
                newValue = Math.max(0, Math.min(8, newValue));

                chromosome.set(i, newValue);
            }
        }
        child.setChromosome(chromosome);
    }
}

