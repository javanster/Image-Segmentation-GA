package com.p3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
        List<List<Integer>> pixels = individual.getImage().getPixels();
        int imageHeight = individual.getImage().getImageHeight();
        int imageLength = individual.getImage().getImageLength();
        Map<Integer, Integer> segmentMap = individual.getSegmentMap();

        for (int i = 0; i < pixels.size(); i++) {
            int segmentIndex = segmentMap.get(i);
            List<Integer> neighboringPixelIndexes = individual.getNeighboringPixelIndexes(i, imageHeight, imageLength);
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
        List<List<Integer>> pixels = individual.getImage().getPixels();
        int imageHeight = individual.getImage().getImageHeight();
        int imageLength = individual.getImage().getImageLength();
        Map<Integer, Integer> segmentMap = individual.getSegmentMap();

        for (int i = 0; i < pixels.size(); i++) {
            for (Integer j : individual.getNeighboringPixelIndexes(i, imageHeight, imageLength)) {
                if (!segmentMap.get(i).equals(segmentMap.get(j))) {
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
        List<List<Integer>> pixels = individual.getImage().getPixels();

        for (Set<Integer> segment : individual.getSegments()) {
            for (Integer i : segment) {
                segmentDeviation += euclideanDistance(pixels.get(i), getCentroid(individual, segment));
            }
        }
        return segmentDeviation;
    }

    /**
     * Returns the centroid of a segment of the image.
     * The centroid is the average RGB values of the pixels in the segment.
     * Helper method for overallDeviation.
     * 
     * @param individual The individual representing the image.
     * @param segment The segment of the image.
     * @return The centroid of the segment.
     */
    private static List<Integer> getCentroid(Individual individual, Set<Integer> segment) {        
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        List<List<Integer>> pixels = individual.getImage().getPixels();

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
        return Math.sqrt(Math.pow(pixel1.get(0) - pixel2.get(0), 2) + 
            Math.pow(pixel1.get(1) - pixel2.get(1), 2) + 
            Math.pow(pixel1.get(2) - pixel2.get(2), 2));
    }

    /**
     * Returns a map where the keys are individuals and the values are their corresponding Pareto ranks.
     *
     * @param population The population of individuals.
     * @return Map where the keys are individuals and the values are their Pareto ranks.
     */
    public static Map<Individual, Integer> getParetoRanks(List<Individual> individuals) {
        Map<Individual, Integer> paretoRanksMap = new HashMap<>();
        List<Individual> remainingIndividuals = new ArrayList<>(individuals);
        int rank = 0;
    
        while (!remainingIndividuals.isEmpty()) {
            Set<Individual> paretoFront = new HashSet<>();
            Iterator<Individual> iterator = remainingIndividuals.iterator();
    
            while (iterator.hasNext()) {
                Individual current = iterator.next();
                boolean isDominated = false;
    
                for (Individual other : remainingIndividuals) {
                    if (current != other && dominates(other, current)) {
                        isDominated = true;
                        break;
                    }
                }
    
                if (!isDominated) {
                    paretoFront.add(current);
                    paretoRanksMap.put(current, rank);
                }
            }
    
            // Remove individuals from the remaining list after identifying the Pareto front
            remainingIndividuals.removeAll(paretoFront);
            rank++;
        }
    
        return paretoRanksMap;
    }
    

    /**
     * Checks if individual1 dominates individual2 based on all three objective functions.
     *
     * @param individual1 The first individual.
     * @param individual2 The second individual.
     * @return True if individual1 dominates individual2, false otherwise.
     */
    private static boolean dominates(Individual individual1, Individual individual2) {
        double edgeValue1 = edgeValue(individual1);
        double edgeValue2 = edgeValue(individual2);
        double connectivityMeasure1 = connectivityMeasure(individual1);
        double connectivityMeasure2 = connectivityMeasure(individual2);
        double overallDeviation1 = overallDeviation(individual1);
        double overallDeviation2 = overallDeviation(individual2);

        // Check if individual1 dominates individual2 based on all three objectives
        return (edgeValue1 >= edgeValue2 && connectivityMeasure1 <= connectivityMeasure2 && overallDeviation1 <= overallDeviation2) &&
                (edgeValue1 > edgeValue2 || connectivityMeasure1 < connectivityMeasure2 || overallDeviation1 < overallDeviation2);
    }

    /**
     * Checks if an individual is dominated by any other individual in a list of individuals.
     *
     * @param individual The individual to be checked.
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

    

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Image image = new Image(imagePath);
        Individual individual = new Individual(image);
        System.out.println(edgeValue(individual));
        System.out.println(connectivityMeasure(individual));
        System.out.println(overallDeviation(individual));

        Population population = new Population(20, imagePath);
        Map<Individual, Integer> paretoRanks = getParetoRanks(population.getIndividuals());
        List<Individual> paretoRank1Individuals = new ArrayList<>();
        List<Individual> paretoRank2Individuals = new ArrayList<>();
        for (Individual ind : paretoRanks.keySet()) {
            if (paretoRanks.get(ind) == 1) {
                paretoRank1Individuals.add(ind);
            } else if (paretoRanks.get(ind) == 2) {
                paretoRank2Individuals.add(ind);
            }
        }
        System.out.println(paretoRanks.keySet().size());
        for (Individual ind : paretoRank1Individuals) {
            System.out.println(isDominated(ind, paretoRank2Individuals));
        }
    }
}

