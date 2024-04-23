package com.p3;

import com.p3.interfaces.CrossoverHandler;
import com.p3.interfaces.MutationHandler;
import com.p3.interfaces.ParentSelector;

/**
 * A class that holds the parameters for the genetic algorithm.
 */
public class Parameters {
    public static int SEGMENTS_LOWEBOUND;
    public static int SEGMENTS_UPPERBOUND;
    public static int POPULATION_SIZE;
    public static Image IMAGE;
    public static ParentSelector PARENT_SELECTOR;
    public static int TOURNAMENT_SIZE;
    public static boolean IS_TOURNAMENT_REPLACEMENT_ALLOWED;
    public static int GENERATIONS;
    public static CrossoverHandler CROSSOVER_HANDLER;
    public static MutationHandler MUTATION_HANDLER;
    public static double MUTATION_PROBABILITY;
    public static int MUTATION_STEP_SIZE;
    public static double EDGE_WEIGHT;
    public static double CONNECTIVITY_WEIGHT;
    public static double DEVIATION_WEIGHT;
}
