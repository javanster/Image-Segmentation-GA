package com.p3;

import com.p3.interfaces.CrossoverHandler;

/**
 * A class that holds the parameters for the genetic algorithm.
 */
public class Parameters {
    public static int POPULATION_SIZE;
    public static int PARENT_COUNT;
    public static int TOURNAMENT_SIZE;
    public static boolean IS_TOURNAMENT_REPLACEMENT_ALLOWED;
    public static int GENERATIONS;
    public static CrossoverHandler CROSSOVER_HANDLER;
}
