package com.p3.interfaces;

import com.p3.Individual;

public interface CrossoverHandler {
    
    /**
     * Crosses two individuals to create two children.
     * 
     * @param parent1 The first parent.
     * @param parent2 The second parent.
     * @return An array of two children.
     */
    public Individual[] cross(Individual parent1, Individual parent2);
}
