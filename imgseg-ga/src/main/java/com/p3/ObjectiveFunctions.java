package com.p3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectiveFunctions {

    // Private constructor to prevent instantiation
    private ObjectiveFunctions() {
        throw new UnsupportedOperationException("ObjectiveFunctions is a utility class and should not be instantiated.");
    }

    public static double edgeValue(Individual individual, List<Set<Integer>> segments) {
        double edgeValue = 0.0;
        List<List<Integer>> pixels = individual.getPixels();
        int imageHeight = individual.getImageHeight();
        int imageLength = individual.getImageLength();

        for (int i = 0; i < pixels.size(); i++) {
            // j should iterate through the neigbours of i
            for (Integer j : individual.getNeighboringPixelIndexes(i, imageHeight, imageLength)) {
                if (!inSameSegment(i, j, segments)) {
                    edgeValue += euclideanDistance(pixels.get(i), pixels.get(j));
                }
            }
        }

        return edgeValue;
    }

    private static boolean inSameSegment(int i, int j, List<Set<Integer>> segments) {
        for (Set<Integer> segment : segments) {
            if (segment.contains(i) && segment.contains(j)) {
                return true;
            }
        }
        return false;
    }

    public static double connectivityMeasure(Individual individual, List<Set<Integer>> segments) {
        double connectivityMeasure = 0.0;
        List<List<Integer>> pixels = individual.getPixels();
        int imageHeight = individual.getImageHeight();
        int imageLength = individual.getImageLength();

        for (int i = 0; i < pixels.size(); i++) {
            for (Integer j : individual.getNeighboringPixelIndexes(i, imageHeight, imageLength)) {
                if (!inSameSegment(i, j, segments)) {
                    connectivityMeasure += 1.0 / 8; // alernatively: (double) 1 / individual.getGraphDirection(i, j, imageHeight, imageLength);
                }
            }
        }

        return connectivityMeasure;
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
        List<List<Integer>> pixels = new ArrayList<List<Integer>>() {
            {
                add(List.of(1, 2, 3));
                add(List.of(4, 5, 6));
                add(List.of(7, 8, 9));
                add(List.of(10, 11, 12));
            }
        };
        int imageHeight = 2;
        int imageLength = 2;
        Individual individual = new Individual(pixels, imageHeight, imageLength);
        List<Set<Integer>> segments = new ArrayList<>();
        Set<Integer> segment1 = Set.of(0, 1);
        Set<Integer> segment2 = Set.of(2, 3);
        segments.add(segment1);
        segments.add(segment2);
        System.out.println(edgeValue(individual, segments));
        System.out.println(connectivityMeasure(individual, segments));
    }
}

