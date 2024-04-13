package com.p3;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static List<List<Individual>> getParetoFronts(List<Individual> individuals) {
        List<Individual> remainingIndividuals = new ArrayList<>(individuals);
        List<List<Individual>> paretoFronts = new ArrayList<>();

        while (!remainingIndividuals.isEmpty()) {
            List<Individual> paretoFront = new ArrayList<>();
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
                }
            }

            // Remove individuals from the remaining list after identifying the Pareto front
            remainingIndividuals.removeAll(paretoFront);
            paretoFronts.add(paretoFront);
        }

        return paretoFronts;
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
     * NSGA-II. IEEE Transactions on Evolutionary Computation, 6(2), 185. https://doi.org/10.1109/4235.996017 
     * 
     * @param population The population of individuals.
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
        individualsCopy.sort((i1, i2) -> Double.compare(edgeValue(i1), edgeValue(i2))); // worst first, i.e. min first
        distances.replace(individualsCopy.get(0), Double.POSITIVE_INFINITY);
        distances.replace(individualsCopy.get(l - 1), Double.POSITIVE_INFINITY);
        double minEdgeValue = edgeValue(individualsCopy.get(0));
        double maxEdgeValue = edgeValue(individualsCopy.get(l - 1));
        for (int i = 1; i < l - 1; i++) {
            double distanceValue = distances.get(individualsCopy.get(i));
            double edgeValueOfNext = edgeValue(individualsCopy.get(i + 1));
            double edgeValueOfPrev = edgeValue(individualsCopy.get(i - 1));
            distances.replace(individualsCopy.get(i), (distanceValue + edgeValueOfNext - edgeValueOfPrev) / maxEdgeValue - minEdgeValue);
        }

        // Connectivity measure
        individualsCopy.sort((i1, i2) -> Double.compare(connectivityMeasure(i2), connectivityMeasure(i1))); // worst first, i.e. max value first
        distances.replace(individualsCopy.get(0), Double.POSITIVE_INFINITY);
        distances.replace(individualsCopy.get(l - 1), Double.POSITIVE_INFINITY);
        double minConnectivityMeasure = connectivityMeasure(individualsCopy.get(l - 1));
        double maxConnectivityMeasure = connectivityMeasure(individualsCopy.get(0));
        for (int i = 1; i < l - 1; i++) {
            double distanceValue = distances.get(individualsCopy.get(i));
            double connectivityMeasureOfNext = connectivityMeasure(individualsCopy.get(i + 1));
            double connectivityMeasureOfPrev = connectivityMeasure(individualsCopy.get(i - 1));
            distances.replace(individualsCopy.get(i), (distanceValue + connectivityMeasureOfNext - connectivityMeasureOfPrev) / maxConnectivityMeasure - minConnectivityMeasure);
        }

        // Overall deviation
        individualsCopy.sort((i1, i2) -> Double.compare(overallDeviation(i2), overallDeviation(i1))); // worst first, i.e. max value first
        distances.replace(individualsCopy.get(0), Double.POSITIVE_INFINITY);
        distances.replace(individualsCopy.get(l - 1), Double.POSITIVE_INFINITY);
        double minOverallDeviationValue = overallDeviation(individualsCopy.get(l - 1));
        double maxOverallDeviationValue = overallDeviation(individualsCopy.get(0));
        for (int i = 1; i < l - 1; i++) {
            double distanceValue = distances.get(individualsCopy.get(i));
            double overallDeviationValueNext = overallDeviation(individualsCopy.get(i + 1));
            double overallDeviationValuePrev = overallDeviation(individualsCopy.get(i - 1));
            distances.replace(individualsCopy.get(i), (distanceValue + overallDeviationValueNext - overallDeviationValuePrev) / maxOverallDeviationValue - minOverallDeviationValue);
        }

        return distances;
    }

    

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Image image = new Image(imagePath);
        Individual individual = new Individual(image);
        System.out.println(edgeValue(individual));
        System.out.println(connectivityMeasure(individual));
        System.out.println(overallDeviation(individual));

        Population population = new Population(30, imagePath);
        List<List<Individual>> paretoRanks = getParetoFronts(population.getIndividuals());
        List<Individual> paretoFront = paretoRanks.get(0);
        List<Individual> secondParetoFront = paretoRanks.get(1);
        List<Individual> thirdParetoFront = paretoRanks.get(2);
        for (Individual p : paretoFront) {
            System.out.println(isDominated(p, secondParetoFront));
        }
        for (Individual p : secondParetoFront) {
            System.out.println(isDominated(p, thirdParetoFront));
        }
        

        System.out.println("Getting crowding distances");
        Map<Individual, Double> crowdingDistances = getCrowdingDistances(population.getIndividuals());

        System.out.println(crowdingDistances);

    }
}

