package com.p3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.p3.interfaces.CrossoverHandler;

public class TwoPointCrossover implements CrossoverHandler{

    @Override
    public Individual[] cross(Individual parent1, Individual parent2) {
        Random random = new Random();
        List<Integer> chromosome1 = parent1.getChromosome();
        List<Integer> chromosome2 = parent2.getChromosome();

        int minIndex = random.nextInt(chromosome1.size() - 1);
        int maxIndex = random.nextInt(chromosome1.size() - minIndex) + minIndex;

        List<Integer> child1 = new ArrayList<>(Collections.nCopies(chromosome1.size(), 0));
        List<Integer> child2 = new ArrayList<>(Collections.nCopies(chromosome1.size(), 0));

        for (int i = minIndex; i <= maxIndex; i++) {
            child1.set(i, chromosome1.get(i));
            child2.set(i, chromosome2.get(i));
        }

        for (int i = 0; i < chromosome1.size(); i++) {
            if (i < minIndex || i > maxIndex) {
                child1.set(i, chromosome2.get(i));
                child2.set(i, chromosome1.get(i));
            }
        }

        Individual[] children = new Individual[2];
        children[0] = new Individual(child1, parent1);
        children[1] = new Individual(child2, parent2);

        return children;
    };
}
