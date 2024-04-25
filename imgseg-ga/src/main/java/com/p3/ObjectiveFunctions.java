package com.p3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The ObjectiveFunctions class provides static methods for calculating objective function values
 * used in image segmentation genetic algorithms.
 */
public class ObjectiveFunctions {

    // Private constructor to prevent instantiation
    private ObjectiveFunctions() {
        throw new UnsupportedOperationException("ObjectiveFunctions is a utility class and should not be instantiated.");
    }

    /**
     * Calculates the edge value of an individual based its current segments.
     * The edge value is the sum of the Euclidean distances between neighboring pixels
     * that belong to different segments.
     * Subject to MAXIMIZATION.
     *
     * @param individual The individual representing the image.
     * @return The edge value of the individual.
     */
    public static double edgeValue(Individual individual) {
        double edgeValue = 0.0;
        List<List<Integer>> pixels = Parameters.IMAGE.getPixels();
        int imageHeight = Parameters.IMAGE.getImageHeight();
        int imageWidth = Parameters.IMAGE.getImageWith();
        Map<Integer, Integer> segmentMap = individual.getSegmentMap();

        for (int i = 0; i < pixels.size(); i++) {
            int segmentIndex = segmentMap.get(i);
            List<Integer> neighboringPixelIndexes = individual.getNeighboringPixelIndexes(i, imageHeight, imageWidth);
            for (int j : neighboringPixelIndexes) {
                if (segmentIndex != segmentMap.get(j)) {
                    edgeValue += euclideanDistance(pixels.get(i), pixels.get(j));
                }
            }
        }

        return edgeValue;
    }

    /**
     * Calculates the connectivity measure of an individual based on its current segments.
     * The connectivity measure is the sum of the inverse of the number of neighboring pixels
     * that belong to different segments.
     * Subject to MINIMIZATION.
     *
     * @param individual The individual representing the image.
     * @return The connectivity measure of the individual.
     */
    public static double connectivityMeasure(Individual individual) {
        double connectivityMeasure = 0.0;
        List<List<Integer>> pixels = Parameters.IMAGE.getPixels();
        int imageHeight = Parameters.IMAGE.getImageHeight();
        int imageWidth = Parameters.IMAGE.getImageWith();
        Map<Integer, Integer> segmentMap = individual.getSegmentMap();

        for (int i = 0; i < pixels.size(); i++) {
            int segmentIndex = segmentMap.get(i);
            List<Integer> neighboringPixelIndexes = individual.getNeighboringPixelIndexes(i, imageHeight, imageWidth);
            for (Integer j : neighboringPixelIndexes) {
                if (segmentIndex != segmentMap.get(j)) {
                    connectivityMeasure += 1.0 / 8; // alernatively: (double) 1 / individual.getGraphDirection(i, j, imageHeight, imageLength);
                }
            }
        }

        return connectivityMeasure;
    }

    /**
     * Calculates the overall deviation of an individual based on its current segments.
     * The overall deviation is the sum of the Euclidean distances between each pixel in a segment
     * and the centroid of that segment.
     * Subject to MINIMIZATION.
     *
     * @param individual The individual representing the image.
     * @return The overall deviation of the individual.
     */
    public static double overallDeviation(Individual individual) {
        double segmentDeviation = 0.0;
        List<List<Integer>> pixels = Parameters.IMAGE.getPixels();

        for (Set<Integer> segment : individual.getSegments()) {
            List<Integer> centroid = getCentroid(segment);
            for (Integer pixelIndex : segment) {
                segmentDeviation += euclideanDistance(pixels.get(pixelIndex), centroid);
            }
        }
        return segmentDeviation;
    }

    /**
     * Returns the centroid of a segment of the image.
     * The centroid is the average RGB values of the pixels in the segment.
     * Helper method for overallDeviation.
     * 
     * @param segment The segment of the image.
     * @return The centroid of the segment.
     */
    private static List<Integer> getCentroid(Set<Integer> segment) {        
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        List<List<Integer>> pixels = Parameters.IMAGE.getPixels();

        for (Integer i : segment) {
            List<Integer> pixel = pixels.get(i);
            redSum += pixel.get(0);
            greenSum += pixel.get(1);
            blueSum += pixel.get(2);
        }

        int redCentroid = redSum / segment.size();
        int greenCentroid = greenSum / segment.size();
        int blueCentroid = blueSum / segment.size();

        return List.of(redCentroid, greenCentroid, blueCentroid);
    }

    /**
     * Returns the Euclidean distance between two pixels in the image. The Euclidean distance is calculated
     * as the square root of the sum of the squared differences of the RGB values of the pixels.
     * 
     * @param pixel1 The RGB values of the first pixel.
     * @param pixel2 The RGB values of the second pixel.
     * @return The Euclidean distance between the two pixels.
     */
    public static double euclideanDistance(List<Integer> pixel1, List<Integer> pixel2) {
        return Math.sqrt(Math.pow(pixel1.get(0) - pixel2.get(0), 2)
            + Math.pow(pixel1.get(1) - pixel2.get(1), 2)
            + Math.pow(pixel1.get(2) - pixel2.get(2), 2));
    }

    /**
     * Calculates the Pareto fronts for a given list of individuals.
     *
     * @param individuals the list of individuals
     * @return a list of lists, where each inner lists represents a Pareto front, ordered by rank
     */
    public static List<List<Individual>> getParetoFronts(List<Individual> individuals) {
        
        // Resetting objective values of individuals
        for (Individual individual : individuals) {
            individual.resetObjectiveValues();
        }

        int n = individuals.size();
        int[] dominatedCount = new int[n];
        List<Integer>[] dominates = new ArrayList[n];
    
        for (int i = 0; i < n; i++) {
            dominates[i] = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (dominates(individuals.get(i), individuals.get(j))) {
                        dominates[i].add(j);
                    } else if (dominates(individuals.get(j), individuals.get(i))) {
                        dominatedCount[i]++;
                    }
                }
            }
        }
    
        List<List<Individual>> paretoFronts = new ArrayList<>();
        while (true) {
            List<Individual> paretoFront = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (dominatedCount[i] == 0) {
                    paretoFront.add(individuals.get(i));
                    dominatedCount[i] = -1;
                }
            }
    
            if (paretoFront.isEmpty()) {
                break;
            }
    
            for (Individual individual : paretoFront) {
                for (int i : dominates[individuals.indexOf(individual)]) {
                    dominatedCount[i]--;
                }
            }
    
            paretoFronts.add(paretoFront);
        }
    
        return paretoFronts;
    }

    /**
     * Returns a map where the keys are individuals and the values are their corresponding Pareto front ranks.
     * 
     * @param individuals The list of individuals.
     * @return Map where the keys are individuals and the values are their Pareto front ranks.
     */
    public static Map<Individual, Integer> getParetoFrontsMap(List<Individual> individuals) {
        Map<Individual, Integer> paretoFrontsMap = new HashMap<>();
        List<List<Individual>> paretoFronts = getParetoFronts(individuals);

        for (int i = 0; i < paretoFronts.size(); i++) {
            List<Individual> paretoFront = paretoFronts.get(i);
            for (Individual individual : paretoFront) {
                paretoFrontsMap.put(individual, i);
            }
        }

        return paretoFrontsMap;
    }

    /**
     * Checks if individual1 dominates individual2 based on all three objective functions.
     *
     * @param individual1 The first individual.
     * @param individual2 The second individual.
     * @return True if individual1 dominates individual2, false otherwise.
     */
    private static boolean dominates(Individual individual1, Individual individual2) {
        double edgeValue1 = individual1.getEdgeValue();
        double edgeValue2 = individual2.getEdgeValue();
        double connectivityMeasure1 = individual1.getConnectivityMeasure();
        double connectivityMeasure2 = individual2.getConnectivityMeasure();
        double overallDeviation1 = individual1.getOverallDeviation();
        double overallDeviation2 = individual2.getOverallDeviation();

        // Check if individual1 dominates individual2 based on all three objectives
        return (edgeValue1 >= edgeValue2 && connectivityMeasure1 <= connectivityMeasure2 && overallDeviation1 <= overallDeviation2) &&
                (edgeValue1 > edgeValue2 || connectivityMeasure1 < connectivityMeasure2 || overallDeviation1 < overallDeviation2);
    }

    /**
     * Checks if an individual is dominated by any other individual in a list of individuals.
     *
     * @param individual1 The individual to be checked.
     * @param individuals The list of individuals to be compared against.
     * @return True if the individual is dominated by any other individual in the list, false otherwise.
     */
    private static boolean isDominated(Individual individual1, List<Individual> individuals) {
        for (Individual individual2 : individuals) {
            if (individual1 != individual2 && dominates(individual2, individual1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a map where the keys are individuals and the values are their corresponding crowding distances.
     * 
     * Based on:
     * Deb, K., Pratap, A., Agarwal, S., & Meyarivan, T. (2002). A fast and elitist multiobjective genetic algorithm:
     * NSGA-II. IEEE Transactions on Evolutionary Computation, 6(2), p. 185. https://doi.org/10.1109/4235.996017 
     * 
     * @param individuals The list of individuals.
     * @return Map where the keys are individuals and the values are their crowding distances.
     */
    public static Map<Individual, Double> getCrowdingDistances(List<Individual> individuals) {
        Map<Individual, Double> distances = new HashMap<>(); 
        int l = individuals.size();

        for (Individual individual : individuals) {
            distances.put(individual, 0.0);
        }
        
        List<Individual> individualsCopy = new ArrayList<>(individuals);

        // Edge value
        individualsCopy.sort((i1, i2) -> Double.compare(i2.getEdgeValue(), i1.getEdgeValue())); // best first, i.e. max value first
        distances.replace(individualsCopy.get(0), Double.POSITIVE_INFINITY);
        distances.replace(individualsCopy.get(l - 1), Double.POSITIVE_INFINITY);
        double minEdgeValue = individualsCopy.get(l - 1).getEdgeValue();
        double maxEdgeValue = individualsCopy.get(0).getEdgeValue();
        for (int i = 1; i < l - 1; i++) {
            double distanceValue = distances.get(individualsCopy.get(i));
            double edgeValueOfNext = individualsCopy.get(i + 1).getEdgeValue();
            double edgeValueOfPrev = individualsCopy.get(i - 1).getEdgeValue();
            distances.replace(individualsCopy.get(i), (distanceValue + edgeValueOfNext - edgeValueOfPrev) / maxEdgeValue - minEdgeValue);
        }

        // Connectivity measure
        individualsCopy.sort((i1, i2) -> Double.compare(i1.getConnectivityMeasure(), i2.getConnectivityMeasure())); // best first, i.e. min value first
        distances.replace(individualsCopy.get(0), Double.POSITIVE_INFINITY);
        distances.replace(individualsCopy.get(l - 1), Double.POSITIVE_INFINITY);
        double minConnectivityMeasure = individualsCopy.get(0).getConnectivityMeasure();
        double maxConnectivityMeasure = individualsCopy.get(l - 1).getConnectivityMeasure();
        for (int i = 1; i < l - 1; i++) {
            double distanceValue = distances.get(individualsCopy.get(i));
            double connectivityMeasureOfNext = individualsCopy.get(i + 1).getConnectivityMeasure();
            double connectivityMeasureOfPrev = individualsCopy.get(i - 1).getConnectivityMeasure();
            distances.replace(individualsCopy.get(i), (distanceValue + connectivityMeasureOfNext - connectivityMeasureOfPrev) / maxConnectivityMeasure - minConnectivityMeasure);
        }

        // Overall deviation
        individualsCopy.sort((i1, i2) -> Double.compare(i1.getOverallDeviation(), i2.getOverallDeviation())); // best first, i.e. min value first
        distances.replace(individualsCopy.get(0), Double.POSITIVE_INFINITY);
        distances.replace(individualsCopy.get(l - 1), Double.POSITIVE_INFINITY);
        double minOverallDeviationValue = individualsCopy.get(0).getOverallDeviation();
        double maxOverallDeviationValue = individualsCopy.get(l - 1).getOverallDeviation();
        for (int i = 1; i < l - 1; i++) {
            double distanceValue = distances.get(individualsCopy.get(i));
            double overallDeviationValueNext = individualsCopy.get(i + 1).getOverallDeviation();
            double overallDeviationValuePrev = individualsCopy.get(i - 1).getOverallDeviation();
            distances.replace(individualsCopy.get(i), (distanceValue + overallDeviationValueNext - overallDeviationValuePrev) / maxOverallDeviationValue - minOverallDeviationValue);
        }

        return distances;
    }
    
    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Parameters.IMAGE = new Image(imagePath);
        Parameters.POPULATION_SIZE = 20;
        Parameters.SEGMENTS_LOWEBOUND = 5;
        Parameters.SEGMENTS_UPPERBOUND = 10;
        Individual individual = new Individual(5);
        System.out.println(edgeValue(individual));
        System.out.println(connectivityMeasure(individual));
        System.out.println(overallDeviation(individual));
        Population population = new Population();

        List<List<Individual>> paretoRanks = getParetoFronts(population.getIndividuals());

        List<Individual> paretoFront = paretoRanks.get(0);
        List<Individual> secondParetoFront = paretoRanks.get(1);
        List<Individual> thirdParetoFront = paretoRanks.get(2);

         // Should all print false
        for (Individual p : paretoFront) {
            System.out.println(isDominated(p, secondParetoFront));
        }

        // Should all print false
        for (Individual p : secondParetoFront) {
            System.out.println(isDominated(p, thirdParetoFront));
        }

        System.out.println("Getting crowding distances");
        Map<Individual, Double> crowdingDistances = getCrowdingDistances(population.getIndividuals());

        System.out.println(crowdingDistances);

    }
}

