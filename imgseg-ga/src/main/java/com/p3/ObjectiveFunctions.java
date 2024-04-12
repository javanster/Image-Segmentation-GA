package com.p3;
import java.util.List;
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

        for (int i = 0; i < pixels.size(); i++) {
            // j should iterate through the neigbours of i
            for (Integer j : individual.getNeighboringPixelIndexes(i, imageHeight, imageLength)) {
                if (!inSameSegment(i, j, individual.getSegments())) {
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

        for (int i = 0; i < pixels.size(); i++) {
            for (Integer j : individual.getNeighboringPixelIndexes(i, imageHeight, imageLength)) {
                if (!inSameSegment(i, j, individual.getSegments())) {
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
     * @param segments   The segments of the image.
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
     * Returns true if the pixels at indexes i and j belong to the same segment.
     * Helper method for edgeValue and connectivityMeasure.
     * 
     * @param i The index of the first pixel.
     * @param j The index of the second pixel.
     * @param segments The segments of the image.
     * @return True if the pixels belong to the same segment, false otherwise.
     */
    private static boolean inSameSegment(int i, int j, List<Set<Integer>> segments) {
        for (Set<Integer> segment : segments) {
            if (segment.contains(i) && segment.contains(j)) {
                return true;
            }
        }
        return false;
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

    // main
    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Image image = new Image(imagePath);
        Individual individual = new Individual(image);
        System.out.println(edgeValue(individual));
        System.out.println(connectivityMeasure(individual));
        System.out.println(overallDeviation(individual));
    }
}

