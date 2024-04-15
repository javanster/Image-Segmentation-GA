package com.p3.interfaces;

import com.p3.Individual;

public interface CrossoverHandler {
    
    public Individual[] cross(Individual parent1, Individual parent2);
}
