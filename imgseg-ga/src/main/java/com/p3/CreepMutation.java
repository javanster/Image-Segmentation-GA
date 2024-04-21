package com.p3;

import com.p3.interfaces.MutationHandler;
import java.util.Random;

public class CreepMutation implements MutationHandler {
    private double mutationProbability = 0.55;
    private int stepSize = 7;
    
    /**
     * Mutates an individual.
     * 
     * @param child The individual to mutate.
     * @return void
     */
    @Override
    public void mutate(Individual child) {
        Random random = new Random();
        for (int i = 0; i < child.getChromosome().size(); i++) {
            if (random.nextDouble() < mutationProbability) {
                int currentValue = child.getChromosome().get(i);

                // Generate a random number from a triangular distribution
                double u = random.nextDouble();
                double tempMutationValue = (u < 0.5) ? Math.sqrt(u) : -Math.sqrt(1 - u);
                int mutationValue = (int) (tempMutationValue * stepSize);

                int newValue = currentValue + mutationValue;

                // Ensure the new value is within the valid range [0, 8]
                newValue = Math.max(0, Math.min(8, newValue));

                child.getChromosome().set(i, newValue);
            }
        }
    }
}

