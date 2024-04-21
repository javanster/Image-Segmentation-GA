package com.p3.interfaces;

import com.p3.Individual;

public interface MutationHandler {
    /**
     * Mutates an individual.
     * 
     * @param child The individual to mutate.
     * @return void
     */
    public void mutate(Individual child);
}
