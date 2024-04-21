package com.p3;

import java.util.List;

import com.p3.interfaces.MutationHandler;
import java.util.Random;
import java.util.ArrayList;

public class RandomResettingMutation implements MutationHandler {
    
    /**
     * Mutates an individual.
     * 
     * @param child The individual to mutate.
     * @return void
     */
    @Override
    public void mutate(Individual child) {
        System.out.print("Starting mutation - ");
        int length = child.getChromosome().size();
        List<Integer> chromosome = child.getChromosome();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            if (random.nextInt(1) < 0.005) { // 10/length) {
                // set to a random int in interval [0, 8]
                chromosome.set(i, random.nextInt(9));
                // System.out.println("Mutated");
            }
        }
        // if (newChromosome.equals(chromosome)) {
        //     System.out.println("No mutation");
        // }
        // System.out.println("Mutation halfway - starting update");

        child.updateSegments();
        // child.updateSegments();
        System.out.println("Mutation done");
    }
}
