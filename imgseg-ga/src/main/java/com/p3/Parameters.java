package com.p3;

import com.p3.interfaces.CrossoverHandler;
import com.p3.interfaces.MutationHandler;

/**
 * A class that holds the parameters for the genetic algorithm.
 */
public class Parameters {
    public static String IMAGE_FILE_PATH;
    public static int SEGMENTS_LOWEBOUND;
    public static int SEGMENTS_UPPERBOUND;
    public static int POPULATION_SIZE;
    public static int TOURNAMENT_SIZE;
    public static boolean IS_TOURNAMENT_REPLACEMENT_ALLOWED;
    public static int GENERATIONS;
    public static CrossoverHandler CROSSOVER_HANDLER;
    public static MutationHandler MUTATION_HANDLER;
}
